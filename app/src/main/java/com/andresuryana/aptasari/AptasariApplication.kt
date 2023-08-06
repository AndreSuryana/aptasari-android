package com.andresuryana.aptasari

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.DataVersionHelper
import com.andresuryana.aptasari.util.JsonDataConverter.QuizData
import com.andresuryana.aptasari.util.JsonDataConverter.getJsonFromRaw
import com.andresuryana.aptasari.util.JsonDataConverter.jsonToQuizData
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class AptasariApplication : Application() {

    @Inject
    lateinit var local: LocalDatabase

    private lateinit var versionHelper: DataVersionHelper

    override fun onCreate() {
        super.onCreate()

        // Set UI to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Init data version helper
        versionHelper = DataVersionHelper(applicationContext)

        // Get quiz data from json raw folder
        val jsonString = getJsonFromRaw(applicationContext, R.raw.data_1)
        val quizData = jsonToQuizData(jsonString)

        // Populate database if there is update from main source
        // FIXME: Currently there is no source!
        if (versionHelper.isAnyUpdate(quizData.dataVersion)) {
            populateDatabase(quizData)
        }
    }

    private fun populateDatabase(quizData: QuizData) = runBlocking(Dispatchers.IO) {
        local.levelDao().insertAll(quizData.level)
        local.questionDao().insertAll(quizData.questions)
        local.answerDao().insertAll(quizData.answers)
    }
}