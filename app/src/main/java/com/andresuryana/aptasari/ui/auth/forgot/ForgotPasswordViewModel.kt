package com.andresuryana.aptasari.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _sendEmailAction = MutableSharedFlow<Unit>()
    val sendEmailAction = _sendEmailAction.asSharedFlow()

    fun sendForgotPasswordEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = userRepository.forgotPassword(email)) {
                is Resource.Success -> {
                    if (result.data == true) _sendEmailAction.emit(Unit)
                    else _isError.emit(Pair(R.string.error_send_forgot_password_email, null))
                }

                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
        }
    }
}