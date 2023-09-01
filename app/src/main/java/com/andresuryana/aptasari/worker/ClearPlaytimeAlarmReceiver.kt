package com.andresuryana.aptasari.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andresuryana.aptasari.di.AppModule
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClearPlaytimeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Use kotlin coroutines to perform background tasks
        CoroutineScope(Dispatchers.IO).launch {
            if (context != null) resetUserPlaytime(context)
        }
    }

    private suspend fun resetUserPlaytime(context: Context) {
        try {
            val local = AppModule.provideLocalDatabase(context.applicationContext)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                // Get current user config
                val userConfig = local.userConfigDao().getUserConfigByUserId(userId)

                // Reset playtime duration and update the user config
                userConfig?.let {
                    it.playTimeDuration = 0L
                    local.userConfigDao().updateUserConfig(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val REQUEST_CODE = 201
    }
}