package com.andresuryana.aptasari.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.repository.UserRepository
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

    private val _isError = MutableSharedFlow<Int>()
    val isError = _isError.asSharedFlow()

    private val _registerAction = MutableSharedFlow<Unit>()
    val registerAction = _registerAction.asSharedFlow()

    private var _user: User? = null
    val user: User? = _user

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                if (password == confirmPassword) {
                    val user = userRepository.register(username, email, password)
                    if (user != null) {
                        _registerAction.emit(Unit)
                        _user = user
                    } else {
                        _isError.emit(R.string.error_register)
                    }
                } else {
                    _isError.emit(R.string.error_register_password_not_match)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isError.emit(R.string.error_register)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}