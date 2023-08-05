package com.andresuryana.aptasari.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CHECK
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CHECKING
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CONTINUE
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.CORRECT
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.END
import com.andresuryana.aptasari.ui.quiz.QuizButtonState.WRONG
import com.andresuryana.aptasari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private var _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

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

    data class QuizResult(
        var correctAnswer: Int = 0,
        var wrongAnswer: Int = 0,
        var totalQuestion: Int = 0
    )

    fun getQuestions(levelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = quizRepository.fetchQuestionByLevel(levelId)) {
                is Resource.Success -> {
                    _questions.postValue(result.data ?: emptyList())
                    _quizResult.totalQuestion = result.data?.size ?: 0
                }

                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
        }
    }

    fun buttonClicked() {
        when (_buttonState.value!!) {
            CONTINUE -> nextQuestion()
            END -> viewModelScope.launch { _actionDone.emit(_quizResult) }
            else -> checkAnswer()
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
                if (selectedAnswer?.isCorrect == true) {
                    _buttonState.value = CORRECT
                    _quizResult.correctAnswer += 1
                } else {
                    _buttonState.value = WRONG
                    _quizResult.wrongAnswer += 1
                }
                delay(2000L)

                // Check is this last question
                val currentQuestionIndex = _currentQuestionIndex.value!!
                val questionSize = _questions.value!!.size
                val isLastQuestion = currentQuestionIndex == questionSize - 1

                if (isLastQuestion) {
                    _buttonState.value = END
                    return@launch
                } else {
                    _buttonState.value = CONTINUE
                }
            }
        }
    }

    private fun nextQuestion() {
        // Next question
        _buttonState.value = CHECK
        _currentQuestionIndex.value = _currentQuestionIndex.value?.plus(1)
        selectedAnswer = null

    }
}