package com.andresuryana.aptasari.data.source.firebase

import com.andresuryana.aptasari.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseSourceImpl(private val auth: FirebaseAuth) : FirebaseSource {

    override suspend fun login(email: String, password: String): User? {
        return try {
            // Login
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null)
                User(authResult.user?.uid, authResult.user?.displayName, authResult.user?.email)
            else null
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun loginWithGoogle(token: String): User? {
        return try {
            val credentials = GoogleAuthProvider.getCredential(token, null)
            val authResult = auth.signInWithCredential(credentials).await()
            if (authResult.user != null)
                User(authResult.user?.uid, authResult.user?.displayName, authResult.user?.email)
            else null
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun register(username: String, email: String, password: String): User? {
        return try {
            // Create new user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Update user profile display name
            val userUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            authResult.user?.updateProfile(userUpdate)

            // Return user
            User(authResult?.user?.uid, username, email)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }
}