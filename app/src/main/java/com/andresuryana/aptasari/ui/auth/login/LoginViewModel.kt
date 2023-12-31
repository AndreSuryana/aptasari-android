package com.andresuryana.aptasari.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _loginAction = MutableSharedFlow<Unit>()
    val loginAction = _loginAction.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            when (val result = userRepository.login(email, password)) {
                is Resource.Success -> {
                    if (result.data != null) {
                        _loginAction.emit(Unit)
                    }
                }

                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
            _isLoading.postValue(false)
        }
    }

    fun loginWithGoogle(token: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            // Check token availability
            if (token == null) {
                _isError.emit(Pair(R.string.error_invalid_token_id, null))
                return@launch
            }

            _isLoading.postValue(true)
            when (val result = userRepository.loginWithGoogle(token)) {
                is Resource.Success -> {
                    if (result.data != null) {
                        _loginAction.emit(Unit)
                    }
                }

                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
            _isLoading.postValue(false)
        }
    }
}