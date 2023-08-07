package com.andresuryana.aptasari

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.andresuryana.aptasari.worker.AlarmManagerRunner
import com.andresuryana.aptasari.worker.TargetAlarmReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AptasariApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set UI to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Start reset playtime alarm at midnight
        val alarmRunner = AlarmManagerRunner(
            applicationContext,
            TargetAlarmReceiver::class.java,
            AlarmManagerRunner.TIME_MIDNIGHT,
            AlarmManagerRunner.ALARM_CLEAR_PLAYTIME_REQUEST_CODE
        )
        alarmRunner.start()
    }
}