package com.andresuryana.aptasari.data.source.prefs

import com.andresuryana.aptasari.data.model.User

interface SessionHelper {

    fun isLoggedIn(): Boolean

    fun setCurrentUser(id: String?, name: String?, email: String?)

    fun getCurrentUser(): User?

    fun clearSession()

    fun setUserFirstQuiz(value: Boolean)

    fun isUserFirstQuiz(): Boolean

}