package com.andresuryana.aptasari.ui.quiz

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.AnswerAdapter
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.databinding.FragmentQuizBinding
import com.andresuryana.aptasari.util.Ext.formatTimer
import com.andresuryana.aptasari.util.QuizType
import com.andresuryana.aptasari.util.RecorderStatus
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<QuizViewModel>()

    private lateinit var answerAdapter: AnswerAdapter

    private var levelId: String? = null

    private var mediaPlayer: MediaPlayer? = null

    private var isRecording = false

    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                context,
                getString(R.string.success_request_audio_record_permission),
                Toast.LENGTH_SHORT
            ).show()

            // Set audio file path & start recording
            val timestamp = Calendar.getInstance().timeInMillis
            val filePath = "${context?.externalCacheDir?.absolutePath}/audio_$timestamp.wav"
            viewModel.startRecorder(filePath)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeTimer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start timer
        viewModel.startTimer()

        // Get level id from arguments
        arguments?.getString("level_id")?.let {
            levelId = it
            viewModel.getQuestions(it)
        }

        // Setup adapter
        setupAnswerAdapter()

        // Setup button listener
        setupButtonListener()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()

        // Stop timer
        viewModel.pauseTimer()
        releaseMediaPlayer()

        // Stop recorder
        viewModel.stopRecorder()
    }

    private fun onAnswerClickListener(answer: Answer) {
        if (answerAdapter.isItemClickable) {
            viewModel.selectedAnswer = answer
            answerAdapter.setSelectedItem(answer)
        }
    }

    private fun setupAnswerAdapter() {
        answerAdapter = AnswerAdapter()
        answerAdapter.setOnItemClickListener(this::onAnswerClickListener)
    }

    private fun setupRecyclerView(answers: List<Answer>) {
        // Setup adapter
        answerAdapter.setRecyclerViewTarget(binding.rvAnswer)
        answerAdapter.submitList(answers)
        binding.rvAnswer.apply {
            adapter = answerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupButtonListener() {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            activity?.onBackPressed()
        }
        binding.btnCheck.setOnClickListener {
            viewModel.buttonClicked()
        }
        binding.btnPlayAudio.setOnClickListener {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.seekTo(0) // Reset duration
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }
        binding.btnRecord.setOnClickListener {
            if (!isRecording) {
                // Set audio file path & start recording
                val timestamp = Calendar.getInstance().timeInMillis
                val filePath = "${context?.externalCacheDir?.absolutePath}/audio_$timestamp.wav"
                viewModel.startRecorder(filePath)
            } else viewModel.stopRecorder()
        }
    }

    private fun observeUiState() {
        // Error
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isError.collectLatest { messagePair ->
                    withContext(Dispatchers.Main) {
                        showSnackbarError(messagePair)
                    }
                }
            }
        }

        // Message
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isShowMessage.collectLatest { messagePair ->
                    withContext(Dispatchers.Main) {
                        showSnackbar(messagePair)
                    }
                }
            }
        }

        // Button state
        viewModel.buttonState.observe(viewLifecycleOwner) { state ->
            // Update button ui
            binding.btnCheck.setText(state.buttonText)
            binding.btnCheck.isClickable = state.isClickable
            binding.btnCheck.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), state.color)

            // Only enable click listener on state CHECK
            // otherwise, disable click listener
            answerAdapter.isItemClickable = state == QuizButtonState.CHECK

            // Check if state is CORRECT or WRONG
            // pause playing audio
            if ((state == QuizButtonState.CORRECT || state == QuizButtonState.WRONG)
                && mediaPlayer?.isPlaying == true
            ) {
                mediaPlayer?.stop()
            }
        }

        // Action Done
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionDone.collectLatest { result ->
                    // Stop the timer
                    viewModel.stopTimer()

                    withContext(Dispatchers.Main) {
                        // Show alert dialog with total correct and wrong answer
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.title_quiz_result)
                            .setMessage(
                                getString(
                                    R.string.subtitle_quiz_result,
                                    result.correctAnswer,
                                    result.totalQuestion
                                )
                            )
                            .setCancelable(false)
                            .setPositiveButton(R.string.btn_back_to_level) { _, _ ->
                                findNavController().popBackStack()
                            }
                            .show()
                    }
                }
            }
        }

        // Questions
        viewModel.questions.observe(viewLifecycleOwner) { questions ->
            setUiStateEmptyLevel(questions.isEmpty())
            binding.progressBar.max = questions.size
        }

        // Current question
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            setCurrentQuestion(question)
        }

        // Track current question
        viewModel.currentQuestionIndex.observe(viewLifecycleOwner) { index ->
            binding.progressBar.progress = index + 1
            viewModel.getCurrentQuestion()
        }

        // Timer
        viewModel.timer.observe(viewLifecycleOwner) { timer ->
            binding.tvTimer.text = timer.formatTimer()
        }

        // Recorder status
        viewModel.recorderStatus.observe(viewLifecycleOwner) { status ->
            binding.tvRecordStatus.setText(status.text)
            isRecording = status == RecorderStatus.RECORDING
            binding.btnRecord.setText(
                if (isRecording) R.string.btn_record_stop
                else R.string.btn_record_start
            )
        }
    }

    private fun setCurrentQuestion(question: Question) {
        // Set title
        binding.tvTitle.text = question.title
            ?: when (question.type) {
                QuizType.AUDIO -> getString(R.string.title_quiz_audio)
                QuizType.AUDIO_INPUT -> getString(R.string.title_quiz_audio_input)
                else -> getString(R.string.title_quiz_text)
            }

        // Current question type
        when (question.type) {
            QuizType.TEXT -> setTextQuestion(question)
            QuizType.AUDIO -> setAudioQuestion(question)
            QuizType.AUDIO_INPUT -> setAudioInputQuestion(question)
            else -> Unit
        }

        // Set visibility for rvAnswers
        binding.rvAnswer.isVisible = question.type != QuizType.AUDIO_INPUT
    }

    private fun setTextQuestion(question: Question) {
        // Update ui
        binding.tvQuestion.visibility = View.VISIBLE
        binding.audioPlayerContainer.visibility = View.GONE
        binding.rvAnswer.visibility = View.VISIBLE
        binding.btnRecord.visibility = View.GONE

        // Setup answer recycler view
        setupRecyclerView(question.answers)

        // Set text question
        binding.tvQuestion.text = question.textQuestion

        // Unset media player
        mediaPlayer = null
    }

    private fun setAudioQuestion(question: Question) {
        // Update ui
        binding.tvQuestion.visibility = View.GONE
        binding.audioPlayerContainer.visibility = View.VISIBLE
        binding.rvAnswer.visibility = View.VISIBLE
        binding.btnRecord.visibility = View.GONE

        // Setup answer recycler view
        setupRecyclerView(question.answers)

        // Set audio question
        mediaPlayer = MediaPlayer().apply {
            setDataSource(question.audioPath)
            prepare()
        }
    }

    private fun setAudioInputQuestion(question: Question) {
        // Update ui
        binding.tvQuestion.visibility = View.GONE
        binding.audioPlayerContainer.visibility = View.VISIBLE
        binding.rvAnswer.visibility = View.GONE
        binding.btnRecord.visibility = View.VISIBLE

        // Set audio question
        mediaPlayer = MediaPlayer().apply {
            setDataSource(question.audioPath)
            prepare()
        }

        // Check for audio record permission
        checkAudioRecordPermission()
    }

    private fun checkAudioRecordPermission() {
        // Check for permission
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO
                )
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO
                )
            }
        }
    }

    private fun setUiStateEmptyLevel(isEmpty: Boolean) {
        // Hide recycler view, and show empty icon
        binding.questionContainer.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.ivEmptyData.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
            }
            stop()
            reset()
            release()
        }
        mediaPlayer = null
    }
}