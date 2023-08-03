package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.model.User
import com.andresuryana.aptasari.util.Resource

interface UserRepository {

    suspend fun login(email: String, password: String): Resource<User>

    suspend fun loginWithGoogle(token: String): Resource<User>

    suspend fun register(username: String, email: String, password: String): Resource<User>

    suspend fun logout(): Resource<Boolean>

    suspend fun forgotPassword(email: String): Resource<Boolean>
}