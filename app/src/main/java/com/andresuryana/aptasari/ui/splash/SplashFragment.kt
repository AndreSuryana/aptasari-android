package com.andresuryana.aptasari.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.databinding.FragmentSplashBinding
import com.andresuryana.aptasari.util.DataVersionHelper
import com.andresuryana.aptasari.util.SplashProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SplashViewModel>()

    @Inject
    lateinit var session: SessionHelper

    private lateinit var versionHelper: DataVersionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater)

        // Init data version helper
        versionHelper = DataVersionHelper(requireContext().applicationContext)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup progress bar
        binding.progressBar.max = SplashProgress.values().size

        // Check for updates
        val localDataVersion = versionHelper.getDataVersion()
        viewModel.checkForUpdates(localDataVersion)

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeUiState() {
        // Progress
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            Log.d("SplashFragment", "ordinal=${progress.ordinal}, name=${progress.name}, size=${SplashProgress.values().size}")
            binding.progressBar.progress = progress.ordinal + 1
            binding.tvProgressText.setText(progress.text)

            if (progress == SplashProgress.APP_LAUNCH) {
                navigateNextFragment()
            }
        }

        // Action Data Update
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionDataUpdated.collectLatest { newDataVersion ->
                    versionHelper.setDataVersion(newDataVersion)
                }
            }
        }
    }

    private fun navigateNextFragment() {
        if (isAdded) {
            // If user logged in, navigate to Quiz
            // otherwise, navigate to Onboarding
            val direction = if (session.isLoggedIn()) {
                SplashFragmentDirections.navigateToLevel()
            } else {
                SplashFragmentDirections.navigateToOnboarding()
            }
            findNavController().navigate(direction)
        }
    }
}