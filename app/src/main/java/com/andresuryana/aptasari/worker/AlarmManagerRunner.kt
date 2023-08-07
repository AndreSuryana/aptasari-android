package com.andresuryana.aptasari.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmManagerRunner(
    private val context: Context,
    receiver: Class<*>,
    private val time: Calendar,
    private val requestCode: Int
) {

    private var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var intent: Intent = Intent(context, receiver)
    private var pendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    fun start() {
        if (!isAlarmRunning()) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                time.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancel() {
        if (isAlarmRunning()) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun isAlarmRunning(): Boolean {
        val existingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return existingIntent != null
    }

    companion object {
        val TIME_MIDNIGHT: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val TIME_NOON: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val ALARM_TARGET_LEARNING_REQUEST_CODE = 101
        val ALARM_CLEAR_PLAYTIME_REQUEST_CODE = 102
    }
}