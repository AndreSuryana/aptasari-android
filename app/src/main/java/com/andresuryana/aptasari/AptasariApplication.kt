package com.andresuryana.aptasari

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.DataVersionHelper
import com.andresuryana.aptasari.util.DummyDataFactory.generateLevelData
import com.andresuryana.aptasari.util.DummyDataFactory.generateQuestionData
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class AptasariApplication : Application() {

    @Inject
    lateinit var local: LocalDatabase

    override fun onCreate() {
        super.onCreate()

        // Set UI to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Populate database if there is update from main source
        // FIXME: Currently there is no source!
        val dataVersionHelper = DataVersionHelper(applicationContext)
        if (dataVersionHelper.isAnyUpdate(0L /* Change this value later, and get it from the source */)) {
            populateDatabase()
        }
    }

    private fun populateDatabase() = runBlocking(Dispatchers.IO) {
        val levels = generateLevelData()
        levels.forEach { level ->
            local.levelDao().insert(level)
            generateQuestionData(level.id) { questions, answers ->
                this.launch(Dispatchers.IO) {
                    local.questionDao().insertAll(questions)
                    local.answerDao().insertAll(answers)
                }
            }
        }
    }
}