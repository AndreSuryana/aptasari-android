package com.andresuryana.aptasari.ui.auth.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.databinding.FragmentForgotPasswordBinding
import com.andresuryana.aptasari.util.Ext.isEmail
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ForgotPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup button listener
        setupButtonListener()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonListener() {
        binding.btnSend.setOnClickListener {
            validateUserInput { email ->
                viewModel.sendForgotPasswordEmail(email)
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateUserInput(result: (email: String) -> Unit) {
        // Get value
        val email = binding.etEmail.text?.trim().toString()

        // Reset helper text
        binding.tilEmail.helperText = ""

        // Email
        if (email.isEmpty()) {
            binding.tilEmail.apply {
                helperText = getString(R.string.helper_empty_email)
                requestFocus()
            }
            return
        } else if (!email.isEmail()) {
            binding.tilEmail.apply {
                helperText = getString(R.string.helper_invalid_email)
                requestFocus()
            }
            return
        }

        // Return value
        return result(email)
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

        // Register Action
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sendEmailAction.collectLatest {
                    withContext(Dispatchers.Main) {
                        // Set ui state if email sent
                        binding.btnSend.setText(R.string.btn_send_again)
                        showSnackbar(R.string.success_send_forgot_password_email)
                    }
                }
            }
        }
    }
}