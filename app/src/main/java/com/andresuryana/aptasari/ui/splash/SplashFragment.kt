package com.andresuryana.aptasari.ui.splash

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var session: SessionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start animate the progress bar
        startProgressBarAnim()

        // Navigate to next fragment after some delay
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000L)
            withContext(Dispatchers.Main) {
                navigateNextFragment()
            }
            cancel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startProgressBarAnim() {
        ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100).apply {
            duration = 1500L
            start()
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