package com.andresuryana.aptasari.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.util.Ext.goAsync
import com.andresuryana.aptasari.util.Ext.toMinutes
import com.andresuryana.aptasari.worker.WorkerUtils.getCurrentUserConfig
import com.andresuryana.aptasari.worker.WorkerUtils.isNotifyUser

class TargetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        // Check if user has enabled notify learning target, show
        val userConfig = getCurrentUserConfig(context)
        if (isNotifyUser(userConfig)) {
            showNotification(context, userConfig?.notifyDuration)
        }
    }

    private fun showNotification(context: Context?, targetDuration: Long?) {
        if (context != null && targetDuration != null) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Create notification
            val contentTitle = context.getString(R.string.notif_title_learning_target)
            val contentMessage = context.getString(
                R.string.notif_subtitle_learning_target,
                targetDuration.toMinutes().toString()
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(contentTitle)
                .setContentText(contentMessage)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(contentTitle)
                        .setBigContentTitle(contentMessage)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            // Show notification
            notificationManager.notify(0, notification)
        }
    }

    companion object {
        const val CHANNEL_ID = "target_alarm_channel"
        const val CHANNEL_NAME = "Learning Target"
    }
}