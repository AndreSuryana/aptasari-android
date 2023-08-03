package com.andresuryana.aptasari.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val sessionHelper: SessionHelper
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableSharedFlow<Pair<Int?, String?>>()
    val isError = _isError.asSharedFlow()

    private val _levels = MutableLiveData<List<Level>>()
    val levels: LiveData<List<Level>> = _levels

    fun getUser(): LiveData<User?> {
        return MutableLiveData<User?>(sessionHelper.getCurrentUser())
    }

    fun getAllLevel() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            when (val result = quizRepository.fetchLevels()) {
                is Resource.Success -> {
                    _levels.postValue(result.data ?: emptyList())
                }
                is Resource.Error -> _isError.emit(Pair(result.messageRes, result.message))
            }
            _isLoading.postValue(false)
        }
    }
}