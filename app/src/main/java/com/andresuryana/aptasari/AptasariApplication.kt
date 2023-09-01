package com.andresuryana.aptasari

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.andresuryana.aptasari.worker.ClearPlaytimeAlarmReceiver
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar

@HiltAndroidApp
class AptasariApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set UI to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Start reset playtime alarm at midnight
        startClearPlaytimeAlarmWorker(applicationContext)
    }

    private fun startClearPlaytimeAlarmWorker(context: Context) {
        // Get alarm manager from service
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Create alarm pending intent
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            ClearPlaytimeAlarmReceiver.REQUEST_CODE,
            Intent(context, ClearPlaytimeAlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Set alarm to start at 12:00 a.m. (Midnight)
        val triggerTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, alarmIntent),
            alarmIntent
        )
    }
}