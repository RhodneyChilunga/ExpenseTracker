package com.example.expensetracker.auth

data class UserAccount(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String = ""
)

sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthManager {
    private val registeredUsers = mutableMapOf<String, UserAccount>()

    init {
        // Pre-register initial test user as required by test prerequisites
        registeredUsers["chikondi@example.com"] = UserAccount(
            name = "Chikondi Banda",
            email = "chikondi@example.com",
            password = "Secure@123"
        )
    }

    fun isEmailRegistered(email: String): Boolean {
        return registeredUsers.containsKey(email.trim().lowercase())
    }

    fun register(name: String, email: String, password: String, phoneNumber: String = ""): AuthResult {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim().lowercase()
        val trimmedPassword = password.trim()

        // Test Case 3 (ET_REGISTER_003): Registration with empty fields
        if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            return AuthResult.Error("Please fill in all required fields")
        }

        // Test Case 4 (ET_REGISTER_004): Registration with invalid email format
        if (!isValidEmailFormat(trimmedEmail)) {
            return AuthResult.Error("Invalid email format")
        }

        // Test Case 2 (ET_REGISTER_002): Registration with existing email
        if (registeredUsers.containsKey(trimmedEmail)) {
            return AuthResult.Error("Email is already registered")
        }

        // Test Case 1 (ET_REGISTER_001): Successful account registration
        val newAccount = UserAccount(
            name = trimmedName,
            email = trimmedEmail,
            password = trimmedPassword,
            phoneNumber = phoneNumber.trim()
        )
        registeredUsers[trimmedEmail] = newAccount
        return AuthResult.Success("Account registered successfully")
    }

    fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPassword = password.trim()

        // Test Case 8 (ET_LOGIN_004): Login with empty fields
        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            return AuthResult.Error("Please fill in all required fields")
        }

        // Test Case 7 (ET_LOGIN_003): Login with unregistered email
        val user = registeredUsers[trimmedEmail]
        if (user == null) {
            return AuthResult.Error("Email or password is incorrect")
        }

        // Test Case 6 (ET_LOGIN_002): Login with incorrect password
        if (user.password != trimmedPassword) {
            return AuthResult.Error("Email or password is incorrect")
        }

        // Test Case 5 (ET_LOGIN_001): Successful login
        return AuthResult.Success("Login successful")
    }

    fun isValidEmailFormat(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return emailRegex.matches(email.trim())
    }

    fun resetUsers() {
        registeredUsers.clear()
        registeredUsers["chikondi@example.com"] = UserAccount(
            name = "Chikondi Banda",
            email = "chikondi@example.com",
            password = "Secure@123"
        )
    }
}
