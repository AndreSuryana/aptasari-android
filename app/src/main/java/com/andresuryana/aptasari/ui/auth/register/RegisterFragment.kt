package com.andresuryana.aptasari.ui.auth.register

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
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.databinding.FragmentRegisterBinding
import com.andresuryana.aptasari.util.Ext.isEmail
import com.andresuryana.aptasari.util.LoadingUtils.dismissLoadingDialog
import com.andresuryana.aptasari.util.LoadingUtils.showLoadingDialog
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
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
        binding.btnRegister.setOnClickListener {
            validateUserInput { username, email, password, confirmPassword ->
                viewModel.register(username, email, password, confirmPassword)
            }
        }
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.navigateToLogin(user?.email)
            )
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateUserInput(result: (username: String, email: String, password: String, confirmPassword: String) -> Unit) {
        // Get value
        val username = binding.etUsername.text?.trim().toString()
        val email = binding.etEmail.text?.trim().toString()
        val password = binding.etPassword.text?.trim().toString()
        val confirmPassword = binding.etConfirmPassword.text?.trim().toString()

        // Reset helper text
        binding.tilUsername.helperText = ""
        binding.tilEmail.helperText = ""
        binding.tilPassword.helperText = ""
        binding.tilConfirmPassword.helperText = ""

        // Username
        if (username.isEmpty()) {
            binding.tilUsername.apply {
                helperText = getString(R.string.helper_empty_username)
                requestFocus()
            }
            return
        }

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

        // Confirm Password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.apply {
                helperText = getString(R.string.helper_empty_confirm_password)
                requestFocus()
            }
            return
        }

        // Return value
        return result(username, email, password, confirmPassword)
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
                viewModel.registerAction.collectLatest {
                    // Set current registered user
                    user = it
                    withContext(Dispatchers.Main) {
                        // Set UI State to register success
                        setUiSuccessRegister()
                    }
                }
            }
        }
    }

    private fun setUiSuccessRegister() {
        binding.registerLinearContainer.visibility = View.GONE
        binding.registerSuccessLinearContainer.visibility = View.VISIBLE
    }
}