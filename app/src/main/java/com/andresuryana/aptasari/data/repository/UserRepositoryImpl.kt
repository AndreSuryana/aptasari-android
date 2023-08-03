package com.andresuryana.aptasari.data.repository

import android.util.Log
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.source.firebase.FirebaseSource
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.util.Resource
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class UserRepositoryImpl(
    private val firebase: FirebaseSource,
    private val session: SessionHelper
) : UserRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val user = firebase.login(email, password)
            user?.let {
                session.setCurrentUser(it.id, it.username, it.email)
            }
            Resource.Success(user)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Error(null, R.string.error_user_not_found)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun loginWithGoogle(token: String): Resource<User> {
        return try {
            val user = firebase.loginWithGoogle(token)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Resource<User> {
        return try {
            val user = firebase.register(username, email, password)
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message, e)
            Resource.Error(e.message)
        }
    }

    override suspend fun logout(): Resource<Boolean> {
        return try {
            firebase.logout()
            session.clearSession()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun forgotPassword(email: String): Resource<Boolean> {
        return try {
            firebase.forgotPassword(email)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}