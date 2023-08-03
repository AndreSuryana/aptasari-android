package com.andresuryana.aptasari.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _registerAction = MutableSharedFlow<User>()
    val registerAction = _registerAction.asSharedFlow()

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            if (password == confirmPassword) {
                when (val result = userRepository.register(username, email, password)) {
                    is Resource.Success -> {
                        Log.d("RegisterViewModel", "register: user=${result.data}")
                        if (result.data != null) {
                            _registerAction.emit(result.data)
                        }
                    }

                    is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
                }
            } else {
                _isError.emit(Pair(R.string.error_register_password_not_match, null))
            }
            _isLoading.postValue(false)
        }
    }
}