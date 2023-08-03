package com.andresuryana.aptasari.ui.level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.LevelAdapter
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.databinding.FragmentLevelBinding
import com.andresuryana.aptasari.util.LoadingUtils.dismissLoadingDialog
import com.andresuryana.aptasari.util.LoadingUtils.showLoadingDialog
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter
        setupLevelAdapter()

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
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
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
        }
    }

    private fun navigateToQuizFragment(level: Level) {
        // TODO: Implement navigation!
    }

    private fun setUiStateEmptyLevel() {
        // TODO: Implement ui state update!
    }
}