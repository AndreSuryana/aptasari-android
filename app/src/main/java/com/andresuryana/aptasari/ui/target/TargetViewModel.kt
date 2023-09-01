package com.andresuryana.aptasari.ui.target

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
class TargetViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isUpdated = MutableSharedFlow<Unit>()
    val isUpdated = _isUpdated.asSharedFlow()

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

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