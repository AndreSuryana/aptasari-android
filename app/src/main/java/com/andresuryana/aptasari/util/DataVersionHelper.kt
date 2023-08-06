package com.andresuryana.aptasari.util

import android.content.Context
import android.content.SharedPreferences

class DataVersionHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getDataVersion(): Long = prefs.getLong(KEY_DATA_VERSION, -1)

    fun setDataVersion(version: Long) {
        prefs.edit().putLong(KEY_DATA_VERSION, version).apply()
    }

    companion object {
        private const val PREFS_NAME = "app_data"
        private const val KEY_DATA_VERSION = "data_version"
    }
}