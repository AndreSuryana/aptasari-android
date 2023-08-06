package com.andresuryana.aptasari.util

import android.content.Context
import android.content.SharedPreferences

class DataVersionHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Check update with comparing last update timestamp from original source,
     * with current update timestamp in the local prefs
     */
    fun isAnyUpdate(sourceDataVersion: Long): Boolean {
        // Get current last update timestamp
        val localDataVersion = prefs.getLong(KEY_DATA_VERSION, -1L)

        return if (localDataVersion == -1L) {
            // Put data if there is data version not found
            prefs.edit().putLong(KEY_DATA_VERSION, sourceDataVersion).apply()
            true
        }
        else localDataVersion != sourceDataVersion && localDataVersion < sourceDataVersion
    }

    companion object {
        private const val PREFS_NAME = "app_data"
        private const val KEY_DATA_VERSION = "data_version"
    }
}