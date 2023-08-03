package com.andresuryana.aptasari.ui.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun updateUserConfig(userId: String?, isNotifyTarget: Boolean, notifyDuration: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            userId?.let {
                userRepository.updateUserNotificationConfig(it, isNotifyTarget, notifyDuration)
            }
        }
    }
}