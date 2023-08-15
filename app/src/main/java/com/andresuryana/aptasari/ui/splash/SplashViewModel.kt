package com.andresuryana.aptasari.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.aptasari.data.model.QuizData
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.FileDownloader
import com.andresuryana.aptasari.util.FileDownloader.Companion.getFilenameFromUrl
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

    // Map for storing level & question <id, filename>
    private val levelsMap = mutableMapOf<String, String>()
    private val questionMap = mutableMapOf<String, String>()

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
                local.levelDao().insertAll(quizData.getLevel())
                local.questionDao().insertAll(quizData.getQuestions())
                local.answerDao().insertAll(quizData.getAnswers())

                delay(500L)
                updateProgress(POPULATE_SUCCESS)

                // Get level images & question audio links
                val links = mutableListOf<String>()
                val imageLevelLinks = getImageLevelLinks(quizData)
                val audioQuestionLinks = getAudioQuestionLinks(quizData)
                links.addAll(imageLevelLinks)
                links.addAll(audioQuestionLinks)

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

    private fun getImageLevelLinks(quizData: QuizData): List<String> {
        val links = mutableListOf<String>()
        quizData.getLevel().forEach {
            links.add(it.iconPath)
            levelsMap[it.id] = getFilenameFromUrl(it.iconPath)
        }
        return links
    }

    private fun getAudioQuestionLinks(quizData: QuizData): List<String> {
        val links = mutableListOf<String>()
        quizData.getQuestions().forEach {
            if (it.audioPath != null) {
                links.add(it.audioPath)
                questionMap[it.id] = getFilenameFromUrl(it.audioPath)
            }
        }
        return links
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
            files.forEach { file ->
                // Update icon path in level table
                levelsMap.forEach { (levelId, filename) ->
                    if (file.name.equals(filename)) {
                        local.levelDao().updateLevelIconPath(levelId, file.absolutePath)
                    }
                }

                // Update audio path in question table
                questionMap.forEach { (questionId, filename) ->
                    if (file.name.equals(filename)) {
                        local.questionDao().updateQuestionAudioPath(questionId, file.absolutePath)
                    }
                }
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