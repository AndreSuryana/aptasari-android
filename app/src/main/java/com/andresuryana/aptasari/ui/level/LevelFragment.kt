package com.andresuryana.aptasari.ui.level

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.LevelAdapter
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.databinding.FragmentLevelBinding
import com.andresuryana.aptasari.di.AppModule
import com.andresuryana.aptasari.util.LoadingUtils.dismissLoadingDialog
import com.andresuryana.aptasari.util.LoadingUtils.showLoadingDialog
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LevelFragment : Fragment() {

    private var _binding: FragmentLevelBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LevelViewModel>()

    private lateinit var levelAdapter: LevelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLevelBinding.inflate(layoutInflater)

        // Request data
        viewModel.getAllLevel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter
        setupLevelAdapter()

        // Setup search bar
        setupSearchBar()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupLevelAdapter() {
        levelAdapter = LevelAdapter()
        levelAdapter.setOnItemClickListener { level ->
            navigateToQuizFragment(level)
        }
        binding.rvLevel.apply {
            adapter = levelAdapter
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                levelAdapter.filter.filter(binding.etSearch.text)
                true
            } else false
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                levelAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun observeUiState() {
        // Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) activity?.showLoadingDialog()
            else activity?.dismissLoadingDialog()
        }

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

        // User
        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            binding.tvGreeting.text =
                if (user != null) getString(R.string.title_greeting, user.username)
                else getString(R.string.title_greeting_anonymous)
        }

        // Level
        viewModel.levels.observe(viewLifecycleOwner) { levels ->
            levelAdapter.setList(levels)
            setUiStateEmptyLevel(levels.isEmpty())
        }
    }

    private fun navigateToQuizFragment(level: Level) {
        // Init session helper
        val session = AppModule.provideSessionHelper(requireContext().applicationContext)

        // If first time navigate to quiz fragment, show target fragment first
        // then continue if target is selected
        val direction =
            if (session.isUserFirstQuiz()) LevelFragmentDirections.navigateToTarget(level.id)
            else LevelFragmentDirections.navigateToQuiz(level.id)
        findNavController().navigate(direction)
    }

    private fun setUiStateEmptyLevel(isEmpty: Boolean) {
        // Hide recycler view, and show empty icon
        binding.rvLevel.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.ivEmptyData.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}