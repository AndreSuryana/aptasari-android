package com.andresuryana.aptasari.ui.profile.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateAction = MutableSharedFlow<Unit>()
    val updateAction = _updateAction.asSharedFlow()

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private lateinit var newUser: User

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            when (val result = userRepository.getUserProfile()) {
                is Resource.Success -> {
                    result.data?.let {
                        _user.postValue(it)
                        newUser = it
                    }
                    _isLoading.postValue(false)
                }

                is Resource.Error -> {
                    _isError.emit(Pair(result.messageRes, result.message))
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun updateUserInfo(username: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            // Set new user data
            newUser.username = username
            newUser.address = address

            when (val result = userRepository.updateUserProfile(newUser)) {
                is Resource.Success -> {
                    if (result.data == true) {
                        _updateAction.emit(Unit)
                        _isLoading.postValue(false)
                    }
                }

                is Resource.Error -> {
                    _isError.emit(Pair(result.messageRes, result.message))
                    _isLoading.postValue(false)
                }
            }
        }
    }
}