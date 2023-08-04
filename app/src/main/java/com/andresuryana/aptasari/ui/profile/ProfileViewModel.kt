package com.andresuryana.aptasari.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _logoutAction = MutableSharedFlow<Unit>()
    val logoutAction = _logoutAction.asSharedFlow()

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = userRepository.logout()) {
                is Resource.Success -> {
                    _logoutAction.emit(Unit)
                }

                is Resource.Error -> {
                    _isError.emit(Pair(result.messageRes, result.message))
                }
            }
        }
    }
}