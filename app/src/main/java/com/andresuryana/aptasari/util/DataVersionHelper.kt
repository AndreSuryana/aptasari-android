package com.andresuryana.aptasari.util

import android.content.Context
import android.content.SharedPreferences

class DataVersionHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Check update with comparing last update timestamp from original source,
     * with current update timestamp in the local prefs
     */
    fun isAnyUpdate(sourceLastUpdateTimestamp: Long): Boolean {
        // Get current last update timestamp
        val currentLastUpdateTimestamp = prefs.getLong(KEY_LAST_UPDATE_TIMESTAMP, -1)

        // Update the value with source last update
        prefs.edit().putLong(KEY_LAST_UPDATE_TIMESTAMP, sourceLastUpdateTimestamp).apply()

        return if (currentLastUpdateTimestamp == -1L) true
        else currentLastUpdateTimestamp != sourceLastUpdateTimestamp && currentLastUpdateTimestamp < sourceLastUpdateTimestamp
    }

    companion object {
        private const val PREFS_NAME = "data_version"
        private const val KEY_LAST_UPDATE_TIMESTAMP = "last_update_timestamp"
    }
}