package com.andresuryana.aptasari.ui.profile.account

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
import com.andresuryana.aptasari.databinding.FragmentAccountBinding
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
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button listener
        setupButtonListener()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonListener() {
        // Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Save
        binding.btnSave.setOnClickListener {
            validateUserInput { username, address ->
                viewModel.updateUserInfo(username, address)
            }
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

        // User profile info
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etUsername.setText(user.username ?: "")
            binding.etAddress.setText(user.address ?: "")
        }

        // Register Action
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateAction.collectLatest {
                    withContext(Dispatchers.Main) {
                        showSnackbar(R.string.success_update_general)
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun validateUserInput(result: (username: String, address: String) -> Unit) {
        // Get value
        val username = binding.etUsername.text?.trim().toString()
        val address = binding.etAddress.text?.trim().toString()

        // Reset helper text
        binding.tilUsername.helperText = ""
        binding.tilAddress.helperText = ""

        // Username
        if (username.isEmpty()) {
            binding.tilUsername.apply {
                helperText = getString(R.string.helper_empty_username)
                requestFocus()
            }
            return
        }

        // Return value
        return result(username, address)
    }
}