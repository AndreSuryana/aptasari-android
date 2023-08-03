package com.andresuryana.aptasari.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.repository.UserRepository
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

    private val _isError = MutableSharedFlow<Int>()
    val isError = _isError.asSharedFlow()

    private val _loginAction = MutableSharedFlow<Unit>()
    val loginAction = _loginAction.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val user = userRepository.login(email, password)
                if (user != null) {
                    _loginAction.emit(Unit)
                } else {
                    _isError.emit(R.string.error_login)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isError.emit(R.string.error_login)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}