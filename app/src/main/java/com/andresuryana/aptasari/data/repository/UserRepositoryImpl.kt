package com.andresuryana.aptasari.data.repository

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.data.source.firebase.FirebaseSource
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.data.source.local.entity.UserConfigEntity
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.util.Resource
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class UserRepositoryImpl(
    private val firebase: FirebaseSource,
    private val session: SessionHelper,
    private val local: LocalDatabase
) : UserRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val user = firebase.login(email, password)
            user?.let {
                session.setCurrentUser(it.id, it.username, it.email)
                local.userConfigDao().insert(UserConfigEntity(0, it.id!!, false))
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

    override suspend fun updateUserNotificationConfig(
        userId: String,
        isNotifyTarget: Boolean,
        notifyDuration: Long
    ): Resource<Boolean> {
        return try {
            val userConfig = local.userConfigDao().getUserConfigByUserId(userId)
            if (userConfig != null) {
                userConfig.isNotifyTarget = isNotifyTarget
                userConfig.notifyDuration = notifyDuration
                local.userConfigDao().updateUserConfig(userConfig)
                Resource.Success(true)
            } else throw NotFoundException("User config not found")
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateUserPlayTime(userId: String, playTime: Long): Resource<Boolean> {
        return try {
            val userConfig = local.userConfigDao().getUserConfigByUserId(userId)
            if (userConfig != null) {
                userConfig.playTimeDuration = playTime
                local.userConfigDao().updateUserConfig(userConfig)
                Resource.Success(true)
            } else throw NotFoundException("User config not found")
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}