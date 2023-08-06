package com.andresuryana.aptasari.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.model.QuizData
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.SplashProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val local: LocalDatabase
) : ViewModel() {

    private val _progress = MutableLiveData<SplashProgress>()
    val progress: LiveData<SplashProgress> = _progress

    private val _actionDataUpdated = MutableSharedFlow<Long>()
    val actionDataUpdated = _actionDataUpdated.asSharedFlow()

    private var quizData: QuizData? = null

    fun checkForUpdates(localDataVersion: Long) {
        viewModelScope.launch {
            try {
                delay(500L)
                _progress.value = SplashProgress.CHECKING_UPDATES

                delay(500L)
                quizData = quizRepository.fetchQuizData()

                if (localDataVersion != quizData!!.dataVersion) {
                    _progress.value = SplashProgress.UPDATING_DATA

                    delay(500L)
                    populateDatabase()
                    _actionDataUpdated.emit(quizData!!.dataVersion)
                }

                delay(500L)
                _progress.value = SplashProgress.APP_LAUNCH
            } catch (e: Exception) {
                delay(500L)
                _progress.value = SplashProgress.FAILED_CHECKING_UPDATES

                delay(500L)
                _progress.value = SplashProgress.RECHECK_UPDATES
                checkForUpdates(localDataVersion)
            }
        }
    }

    private suspend fun populateDatabase() {
        delay(500L)
        _progress.value = SplashProgress.POPULATING_DATA
        quizData?.let {
            try {
                local.levelDao().insertAll(it.level)
                local.questionDao().insertAll(it.questions)
                local.answerDao().insertAll(it.answers)

                _progress.value = SplashProgress.POPULATE_SUCCESS
                delay(500L)
            } catch (e: Exception) {
                _progress.value = SplashProgress.POPULATE_ERROR
            }
        }
    }
}