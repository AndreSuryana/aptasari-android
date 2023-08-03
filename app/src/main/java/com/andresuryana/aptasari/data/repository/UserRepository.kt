package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.model.User

interface UserRepository {

    suspend fun login(email: String, password: String): User?

    suspend fun loginWithGoogle(token: String): User?

    suspend fun register(username: String, email: String, password: String): User?

    suspend fun logout()

    suspend fun forgotPassword(email: String)
}