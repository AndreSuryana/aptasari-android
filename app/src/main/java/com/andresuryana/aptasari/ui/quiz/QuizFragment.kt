package com.andresuryana.aptasari.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.AnswerAdapter
import com.andresuryana.aptasari.adapter.QuestionAdapter
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.databinding.FragmentQuizBinding
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<QuizViewModel>()

    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var answerAdapter: AnswerAdapter

    private var levelId: String? = null

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

        // Get level id from arguments
        arguments?.getString("level_id")?.let {
            levelId = it
            viewModel.getQuestions(it)
        }

        // Setup view pager adapter, init adapter first important!
        setupAnswerAdapter()
        setupPagerAdapter()

        // Setup button listener
        setupButtonListener()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }

    private fun onAnswerClickListener(answer: Answer) {
        if (answerAdapter.isItemClickable) {
            viewModel.selectedAnswer = answer
            answerAdapter.setSelectedItem(answer)
        }
    }

    private fun setupPagerAdapter() {
        // Setup view pager
        questionAdapter = QuestionAdapter(answerAdapter)
        binding.viewPager.apply {
            adapter = questionAdapter
            isUserInputEnabled = false
            (getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun setupAnswerAdapter() {
        answerAdapter = AnswerAdapter()
        answerAdapter.setOnItemClickListener(this::onAnswerClickListener)
    }

    private fun setupButtonListener() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnCheck.setOnClickListener {
            viewModel.buttonClicked()
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
        }

        // Action Done
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionDone.collectLatest { result ->
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
            questionAdapter.submitList(questions)
            setUiStateEmptyLevel(questions.isEmpty())
            binding.progressBar.max = questions.size
        }

        // Track current question
        viewModel.currentQuestionIndex.observe(viewLifecycleOwner) { index ->
            binding.viewPager.currentItem = index
            binding.progressBar.progress = index + 1
        }
    }

    private fun setUiStateEmptyLevel(isEmpty: Boolean) {
        // Hide recycler view, and show empty icon
        binding.viewPager.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.ivEmptyData.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}