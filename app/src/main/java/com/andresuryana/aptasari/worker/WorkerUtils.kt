package com.andresuryana.aptasari.worker

import android.content.Context
import com.andresuryana.aptasari.data.source.local.entity.UserConfigEntity
import com.andresuryana.aptasari.di.AppModule
import com.google.firebase.auth.FirebaseAuth

object WorkerUtils {

    suspend fun getCurrentUserConfig(context: Context?): UserConfigEntity? {
        val local = context?.applicationContext?.let { AppModule.provideLocalDatabase(it) }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            if (local != null && userId != null) {
                local.userConfigDao().getUserConfigByUserId(userId)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resetUserPlaytime(context: Context?) {
        try {
            val local = context?.applicationContext?.let { AppModule.provideLocalDatabase(it) }
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (local != null && userId != null) {
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

    fun isNotifyUser(userConfig: UserConfigEntity): Boolean {
        return userConfig.isNotifyTarget && userConfig.playTimeDuration < userConfig.notifyDuration
    }
}