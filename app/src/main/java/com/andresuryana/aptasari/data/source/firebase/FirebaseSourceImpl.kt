package com.andresuryana.aptasari.data.source.firebase

import com.andresuryana.aptasari.data.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseSourceImpl(
    private val auth: FirebaseAuth,
    private val firebaseDB: FirebaseDatabase
) : FirebaseSource {

    override suspend fun login(email: String, password: String): User? {
        return try {
            // Login
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                val user = User(authResult.user?.uid, authResult.user?.displayName, authResult.user?.email)
                // Store the user's profile in the database
                storeUserProfile(user)
                user
            } else null
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun loginWithGoogle(token: String): User? {
        return try {
            val credentials = GoogleAuthProvider.getCredential(token, null)
            val authResult = auth.signInWithCredential(credentials).await()
            if (authResult.user != null) {
                val user = User(authResult.user?.uid, authResult.user?.displayName, authResult.user?.email)
                // Store the user's profile in the database
                storeUserProfile(user)
                user
            } else null
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

            // Send email verification
            authResult.user?.sendEmailVerification()

            // Create a User object
            val user = User(authResult?.user?.uid, username, email)

            // Store the user's profile in the database
            storeUserProfile(user)

            user
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

    override suspend fun getUserProfile(): User? {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userRef = firebaseDB.reference.child("users").child(currentUser.uid)
                val snapshot = Tasks.await(userRef.get())
                return snapshot.getValue(User::class.java)
            }
            return null
        } catch (e: DatabaseException) {
            // Handle Firebase Realtime Database specific exceptions
            throw e
        } catch (e: Exception) {
            // Handle other exceptions
            throw e
        }
    }

    override suspend fun updateUserProfile(newUser: User): Boolean {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userRef = firebaseDB.reference.child("users").child(currentUser.uid)
                Tasks.await(userRef.setValue(newUser))
                return true // Update successful
            }
            return false
        } catch (e: DatabaseException) {
            // Handle Firebase Realtime Database specific exceptions
            throw e
        } catch (e: Exception) {
            // Handle other exceptions
            throw e
        }
    }

    private suspend fun storeUserProfile(user: User) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = firebaseDB.reference.child("users").child(currentUser.uid)

            try {
                // Check if the user data already exists at the specified location
                val snapshot = Tasks.await(userRef.get())
                if (!snapshot.exists()) {
                    userRef.setValue(user).await()
                }
            } catch (e: DatabaseException) {
                // Handle database-specific exceptions
                throw e
            } catch (e: Exception) {
                // Handle other exceptions
                throw e
            }
        }
    }
}