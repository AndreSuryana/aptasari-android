package com.andresuryana.aptasari.ui.target

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.data.source.local.entity.UserConfigEntity
import com.andresuryana.aptasari.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userConfig = MutableLiveData<UserConfigEntity>()
    val userConfig: LiveData<UserConfigEntity> = _userConfig

    private val _isUpdated = MutableSharedFlow<Unit>()
    val isUpdated = _isUpdated.asSharedFlow()

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    init {
        getUserConfig()
    }

    private fun getUserConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            // Get current user id
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                when (val result = userRepository.getUserConfig(userId)) {
                    is Resource.Success -> {
                        result.data?.let {
                            _userConfig.postValue(it)
                        }
                    }

                    is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
                }
            }
        }
    }

    fun updateUserConfig(userId: String?, isNotifyTarget: Boolean, notifyDuration: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            userId?.let {
                when (val result = userRepository.updateUserNotificationConfig(
                    it,
                    isNotifyTarget,
                    notifyDuration
                )) {
                    is Resource.Success -> {
                        if (result.data == true) {
                            _isUpdated.emit(Unit)
                        }
                    }

                    is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
                }
            }
        }
    }
}