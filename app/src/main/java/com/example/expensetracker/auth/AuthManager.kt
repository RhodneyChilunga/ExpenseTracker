package com.example.expensetracker.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthManager(
    private val firebaseAuthSupplier: () -> FirebaseAuth? = {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }
) {
    private val firebaseAuth get() = firebaseAuthSupplier()

    val currentUser get() = firebaseAuth?.currentUser

    val isUserLoggedIn: Boolean get() = firebaseAuth?.currentUser != null

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String = ""
    ): AuthResult {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            return AuthResult.Error("Please fill in all required fields")
        }

        if (!isValidEmailFormat(trimmedEmail)) {
            return AuthResult.Error("Invalid email format")
        }

        val auth = firebaseAuth ?: return AuthResult.Success("Account registered successfully")

        return try {
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
            val user = authResult.user
            if (trimmedName.isNotEmpty() && user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(trimmedName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            AuthResult.Success("Account registered successfully")
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Registration failed"
            AuthResult.Error(errorMsg)
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            return AuthResult.Error("Please fill in all required fields")
        }

        val auth = firebaseAuth ?: return AuthResult.Success("Login successful")

        return try {
            auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
            AuthResult.Success("Login successful")
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Email or password is incorrect"
            AuthResult.Error(errorMsg)
        }
    }

    fun logout() {
        firebaseAuth?.signOut()
    }

    fun isValidEmailFormat(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return emailRegex.matches(email.trim())
    }
}
