package com.andresuryana.aptasari.ui.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CHECK
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CHECKING
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CONTINUE
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CORRECT
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.END
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.WAITING_AUDIO
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.WRONG
import com.andresuryana.aptasari.util.QuizType.AUDIO_INPUT
import com.andresuryana.aptasari.util.RecorderStatus
import com.andresuryana.aptasari.util.RecorderStatus.RECORDING
import com.andresuryana.aptasari.util.RecorderStatus.STOPPED
import com.andresuryana.aptasari.util.RecorderStatus.WAITING
import com.andresuryana.aptasari.util.Resource
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private var _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _isShowMessage = MutableSharedFlow<Pair<Int?, String?>>()
    val isShowMessage = _isShowMessage.asSharedFlow()

    private val _buttonState = MutableLiveData(CHECK)
    val buttonState: LiveData<QuizButtonState> = _buttonState

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _actionDone = MutableSharedFlow<QuizResult>()
    val actionDone = _actionDone.asSharedFlow()

    private val _quizResult = QuizResult()

    var selectedAnswer: Answer? = null

    // Timer Variables
    private var timerJob: Job? = null
    private var isTimerPaused: Boolean = false

    private val _timer = MutableLiveData(0L)
    val timer: LiveData<Long> = _timer

    // Recorder Variables
    private var recorder: WaveRecorder? = null
    private var audioFilePath: String? = null

    private val _recorderStatus = MutableLiveData(WAITING)
    val recorderStatus: LiveData<RecorderStatus> = _recorderStatus

    data class QuizResult(
        var correctAnswer: Int = 0,
        var wrongAnswer: Int = 0,
        var totalQuestion: Int = 0
    )

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun getQuestions(levelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = quizRepository.fetchQuestionByLevel(levelId)) {
                is Resource.Success -> {
                    _questions.postValue(result.data ?: emptyList())
                    _quizResult.totalQuestion = result.data?.size ?: 0

                    // Set first question if data not null
                    if (result.data != null) {
                        if (result.data.isNotEmpty())
                            _currentQuestion.postValue(result.data.first())
                    }
                }

                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
        }
    }

    fun getCurrentQuestion() {
        if (_questions.value != null) {
            _currentQuestion.value = _questions.value?.get(_currentQuestionIndex.value!!)

            // Add condition where type is AUDIO INPUT
            // Set button state WAITING_AUDIO
            if (_currentQuestion.value?.type == AUDIO_INPUT) {
                _buttonState.value = WAITING_AUDIO
            }
        }
    }

    fun buttonClicked() {
        when (_buttonState.value!!) {
            CONTINUE -> nextQuestion()
            END -> viewModelScope.launch { _actionDone.emit(_quizResult) }
            else -> if (_currentQuestion.value!!.type == AUDIO_INPUT) predictAudioInput() else checkAnswer()
        }
    }

    fun startTimer() {
        if (timerJob?.isActive != true) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    if (!isTimerPaused) _timer.postValue(_timer.value?.plus(1000L))
                }
            }
        }
    }

    fun pauseTimer() {
        if (timerJob?.isActive == true && !isTimerPaused) {
            isTimerPaused = true
            timerJob?.cancel()
        }
    }

    fun resumeTimer() {
        if (isTimerPaused) {
            isTimerPaused = false
            startTimer()
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                    userRepository.updateUserPlayTime(userId, _timer.value ?: 0L)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                timerJob?.cancel()
            }
        }
    }

    fun startRecorder() {
        if (recorder != null && _recorderStatus.value == WAITING) {
            // Start audio recording
            recorder?.startRecording()

            // Update ui state for audio recording
            _recorderStatus.postValue(RECORDING)
        }
    }

    fun stopRecorder() {
        if (recorder != null && _recorderStatus.value == RECORDING) {
            // Stop recorder and get the audio file
            recorder?.stopRecording()
            recorder = null

            // Update ui state for audio recording
            _buttonState.value = CHECK
            _recorderStatus.postValue(STOPPED)
        }
    }

    fun initAudioRecorder(path: String) {
        // Initialize audio recorder
        if (recorder == null) {
            recorder = WaveRecorder(path)
            audioFilePath = path
        }

        // Setup noise suppressor
        recorder?.noiseSuppressorActive = true

        // Add state change listener
        recorder?.onStateChangeListener = { state ->
            when (state) {
                RecorderState.RECORDING -> {
                    _recorderStatus.value = RECORDING
                }

                RecorderState.STOP -> {
                    _recorderStatus.value = STOPPED
                }

                else -> Unit
            }
        }
    }

    private fun checkAnswer() {
        viewModelScope.launch {
            // Get current question
            val question: Question? = _questions.value?.get(_currentQuestionIndex.value!!)

            if (selectedAnswer == null) _isShowMessage.emit(
                Pair(
                    R.string.error_quiz_no_selected_answer,
                    null
                )
            )

            // Check if there is selected answer
            if (selectedAnswer != null && question != null
                && selectedAnswer?.questionId?.equals(question.id) == true
            ) {
                // Update button state
                if (_buttonState.value == CHECK) {
                    _buttonState.value = CHECKING
                }

                // Check answer if false return with updated button state
                // Simulate process with delay, also to make sure button state changed smoothly
                delay(1000L)
                calculateCorrectAnswer(selectedAnswer?.isCorrect == true)

                // Check is current question is the last question
                delay(2000L)
                checkIsLastQuestion()
            }
        }
    }

    private fun predictAudioInput() {
        viewModelScope.launch {
            // Get current question
            val question: Question? = _questions.value?.get(_currentQuestionIndex.value!!)

            // Make sure type is AUDIO_INPUT & audioFile is not null
            if (question?.type != AUDIO_INPUT && question?.actualClass == null) {
                _isError.emit(Pair(R.string.error_invalid_audio_input_question, null))
                return@launch
            }

            // Update button state
            if (_buttonState.value == CHECK) {
                _buttonState.value = CHECKING
            }

            // Create audio file from the file path used in the recorder
            audioFilePath?.let { path ->
                val audioFile = File(path)
                when (val result =
                    quizRepository.predictAudio(question.actualClass.toString(), audioFile)) {
                    is Resource.Success -> {
                        // Check the prediction result
                        if (result.data != null) {
                            Log.d(
                                "QuizViewModel",
                                "predictAudioInput: actual=${result.data.actualClass}, predicted=${result.data.predictedClass}"
                            )

                            // Check answer if false return with updated button state
                            calculateCorrectAnswer(result.data.actualClass == result.data.predictedClass)

                            // Check is current question is the last question
                            checkIsLastQuestion()
                        }
                    }

                    is Resource.Error -> {
                        _isError.emit(Pair(result.messageRes, result.message))
                    }
                }
            }
        }
    }

    private fun nextQuestion() {
        // Next question
        _buttonState.value = CHECK
        _currentQuestionIndex.value = _currentQuestionIndex.value?.plus(1)
        selectedAnswer = null
        audioFilePath = null
    }

    private fun checkIsLastQuestion() {
        val currentQuestionIndex = _currentQuestionIndex.value!!
        val questionSize = _questions.value!!.size
        val isLastQuestion = currentQuestionIndex == questionSize - 1

        if (isLastQuestion) {
            _buttonState.value = END
            return
        } else {
            _buttonState.value = CONTINUE
        }
    }

    private fun calculateCorrectAnswer(isCorrect: Boolean) {
        if (isCorrect) {
            _buttonState.value = CORRECT
            _quizResult.correctAnswer += 1
        } else {
            _buttonState.value = WRONG
            _quizResult.wrongAnswer += 1
        }
    }
}