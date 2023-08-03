package com.andresuryana.aptasari.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.databinding.FragmentLoginBinding
import com.andresuryana.aptasari.util.Ext.isEmail
import com.andresuryana.aptasari.util.LoadingUtils.dismissLoadingDialog
import com.andresuryana.aptasari.util.LoadingUtils.showLoadingDialog
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user email from arguments
        arguments?.getString("user_email")?.let { email ->
            // If exists, insert into email input
            binding.etEmail.setText(email)

            // Show dialog message
            showSnackbar(R.string.success_register)
        }

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
        binding.btnLogin.setOnClickListener {
            validateUserInput { email, password ->
                viewModel.login(email, password)
            }
        }
        binding.btnLoginGoogle.setOnClickListener {
            // TODO: Not yet implemented!
            Toast.makeText(context, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show()
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.navigateToRegister()
            )
        }
        binding.btnForgotPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.navigateToForgotPassword()
            )
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateUserInput(result: (email: String, password: String) -> Unit) {
        // Get value
        val email = binding.etEmail.text?.trim().toString()
        val password = binding.etPassword.text?.trim().toString()

        // Reset helper text
        binding.tilEmail.helperText = ""
        binding.tilPassword.helperText = ""

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
        }

        // Password
        if (password.isEmpty()) {
            binding.tilPassword.apply {
                helperText = getString(R.string.helper_empty_password)
                requestFocus()
            }
            return
        }

        // Return value
        return result(email, password)
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

        // Register Action
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginAction.collectLatest {
                    withContext(Dispatchers.Main) {
                        // Navigate to level fragment
                        findNavController().navigate(
                            LoginFragmentDirections.navigateToLevel()
                        )
                    }
                }
            }
        }
    }
}