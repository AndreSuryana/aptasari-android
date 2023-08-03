package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.source.firebase.FirebaseSource
import com.andresuryana.aptasari.data.source.prefs.SessionHelper

class UserRepositoryImpl(
    private val firebase: FirebaseSource,
    private val session: SessionHelper
) : UserRepository {

    override suspend fun login(email: String, password: String): User? {
        val user = firebase.login(email, password)
        user?.let {
            session.setCurrentUser(it.id, it.username, it.email)
        }
        return user
    }

    override suspend fun loginWithGoogle(token: String): User? =
        firebase.loginWithGoogle(token)

    override suspend fun register(username: String, email: String, password: String): User? =
        firebase.register(username, email, password)

    override suspend fun logout() {
        firebase.logout()
        session.clearSession()
    }

    override suspend fun forgotPassword(email: String) {
        firebase.forgotPassword(email)
    }
}