package com.andresuryana.aptasari.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.model.QuizData
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.Ext.removeFileExtension
import com.andresuryana.aptasari.util.FileDownloader
import com.andresuryana.aptasari.util.SplashProgress
import com.andresuryana.aptasari.util.SplashProgress.APP_LAUNCH
import com.andresuryana.aptasari.util.SplashProgress.CHECKING_UPDATES
import com.andresuryana.aptasari.util.SplashProgress.DOWNLOAD_FILES
import com.andresuryana.aptasari.util.SplashProgress.DOWNLOAD_FILES_COMPLETED
import com.andresuryana.aptasari.util.SplashProgress.FAILED_CHECKING_UPDATES
import com.andresuryana.aptasari.util.SplashProgress.POPULATE_ERROR
import com.andresuryana.aptasari.util.SplashProgress.POPULATE_SUCCESS
import com.andresuryana.aptasari.util.SplashProgress.POPULATING_DATA
import com.andresuryana.aptasari.util.SplashProgress.UPDATING_DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val local: LocalDatabase,
    private val fileDownloader: FileDownloader
) : ViewModel(), FileDownloader.DownloadCallback {

    private val _progress = MutableLiveData<SplashProgress>()
    val progress: LiveData<SplashProgress> = _progress

    private val _progressText = MutableLiveData<Pair<Int, String?>>()
    val progressText: LiveData<Pair<Int, String?>> = _progressText

    private val _actionDataUpdated = MutableSharedFlow<Long>()
    val actionDataUpdated = _actionDataUpdated.asSharedFlow()

    private var quizData: QuizData? = null

    fun checkForUpdates(localDataVersion: Long) {
        viewModelScope.launch {
            try {
                delay(500L)
                updateProgress(CHECKING_UPDATES)

                delay(500L)
                quizData = try {
                    quizRepository.fetchQuizData()
                } catch (e: Exception) {
                    updateProgress(FAILED_CHECKING_UPDATES)
                    return@launch
                }

                if (localDataVersion != quizData!!.dataVersion) {
                    updateProgress(UPDATING_DATA)

                    delay(500L)
                    populateDatabase()
                    _actionDataUpdated.emit(quizData!!.dataVersion)
                } else {
                    endProcess()
                }
            } catch (e: Exception) {
                delay(500L)
                updateProgress(FAILED_CHECKING_UPDATES)
            }
        }
    }

    private suspend fun populateDatabase() {
        delay(500L)
        updateProgress(POPULATING_DATA)
        quizData?.let { quizData ->
            try {
                local.levelDao().insertAll(quizData.level)
                local.questionDao().insertAll(quizData.questions)
                local.answerDao().insertAll(quizData.answers)

                delay(500L)
                updateProgress(POPULATE_SUCCESS)

                val links =
                    quizData.questions.filter { it.audioPath != null }.map { it.audioPath!! }
                if (links.isNotEmpty()) {
                    downloadFiles(links)
                } else {
                    endProcess()
                }
            } catch (e: Exception) {
                updateProgress(POPULATE_ERROR)
            }
        }
    }

    private fun downloadFiles(links: List<String>) {
        updateProgress(DOWNLOAD_FILES)
        fileDownloader.downloadFiles(links, this)
    }

    private fun updateProgress(progress: SplashProgress, textAlt: String? = null) {
        _progress.postValue(progress)
        _progressText.postValue(Pair(progress.text, textAlt))
    }

    private fun endProcess() {
        viewModelScope.launch {
            delay(500L)
            updateProgress(APP_LAUNCH)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileDownloader.cancelDownload()
    }

    override fun onDownloadProgress(progress: Long, filename: String) {
        updateProgress(DOWNLOAD_FILES, "Mengunduh file: $filename ... $progress%")
    }

    override fun onDownloadCompleted(files: List<File>) {
        viewModelScope.launch {
            // Get question ids from filename by removing extension
            files.forEach { file ->
                val questionId = file.name.removeFileExtension()
                local.questionDao().updateQuestionAudioPath(questionId, file.absolutePath)
            }

            // After done
            updateProgress(DOWNLOAD_FILES_COMPLETED)
            endProcess()
        }
    }

    override fun onDownloadError(message: String?) {
        updateProgress(SplashProgress.DOWNLOAD_FILES_ERROR, message)
    }
}