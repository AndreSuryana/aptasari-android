package com.andresuryana.aptasari

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AptasariApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set UI to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}