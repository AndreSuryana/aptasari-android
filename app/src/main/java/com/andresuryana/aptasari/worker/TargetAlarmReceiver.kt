package com.andresuryana.aptasari.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.andresuryana.aptasari.MainActivity
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.util.Ext.toMinutes
import com.andresuryana.aptasari.worker.WorkerUtils.getCurrentUserConfig
import com.andresuryana.aptasari.worker.WorkerUtils.isNotifyUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TargetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Use kotlin coroutines to perform background tasks
        CoroutineScope(Dispatchers.IO).launch {
            // Check target alarm notification is enabled
            if (context != null) {
                getCurrentUserConfig(context)?.let { userConfig ->
                    if (isNotifyUser(userConfig)) {
                        showTargetNotification(context, userConfig.notifyDuration)
                    }
                }
            }
        }
    }

    private fun showTargetNotification(context: Context, targetDuration: Long) {
        // Get notification manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for API 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                TARGET_CHANNEL_ID,
                TARGET_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create pending intent for notification
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            TARGET_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Define notification content
        val title = context.getString(R.string.notif_title_learning_target)
        val message = context.getString(
            R.string.notif_subtitle_learning_target,
            targetDuration.toMinutes().toString()
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, TARGET_CHANNEL_ID).apply {
            // Set content
            setContentTitle(title)
            setContentText(message)
            setSmallIcon(R.drawable.ic_alarm)

            // Set priority
            priority = NotificationCompat.PRIORITY_HIGH

            // Set pending intent
            setContentIntent(pendingIntent)
            setAutoCancel(true)

            // Set style
            setStyle(
                NotificationCompat.BigTextStyle(this)
                    .setBigContentTitle(title)
            )
        }.build()

        // Show notification
        notificationManager.notify(TARGET_NOTIFICATION_ID, notification)
    }

    companion object {
        const val TARGET_CHANNEL_ID = "learning_target"
        const val TARGET_CHANNEL_NAME = "Learning Target Notification"

        const val TARGET_NOTIFICATION_ID = 101
    }
}