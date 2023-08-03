package com.andresuryana.aptasari.data.source.prefs

import android.content.Context
import android.content.SharedPreferences
import com.andresuryana.aptasari.data.model.User

class SessionHelperImpl(context: Context) : SessionHelper {

    private var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_USER_ID, null) != null
    }

    override fun setCurrentUser(id: String?, name: String?, email: String?) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USERNAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    override fun getCurrentUser(): User? {
        val id = prefs.getString(KEY_USER_ID, null)
        val username = prefs.getString(KEY_USERNAME, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)

        return if (id != null && username != null && email != null)
            User(id, username, email)
        else null
    }

    override fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val PREFS_NAME = "aptasari_prefs"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_USERNAME = "KEY_USERNAME"
        const val KEY_USER_EMAIL = "KEY_USER_EMAIL"
    }
}