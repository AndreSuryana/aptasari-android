package com.andresuryana.aptasari.data.source.firebase

import com.andresuryana.aptasari.data.model.User

interface FirebaseSource {

    suspend fun login(email: String, password: String): User?

    suspend fun loginWithGoogle(token: String): User?

    suspend fun register(username: String, email: String, password: String): User?

    suspend fun logout()

    suspend fun forgotPassword(email: String)

    suspend fun getUserProfile(): User?

    suspend fun updateUserProfile(newUser: User): Boolean

}