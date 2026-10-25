package com.example.expensetracker

import com.example.expensetracker.auth.AuthManager
import com.example.expensetracker.auth.AuthResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit Tests verifying AuthManager input validation logic for Firebase Authentication.
 */
class AuthUnitTest {

    private val authManager = AuthManager()

    @Test
    fun testCase_ET_REGISTER_003_registrationWithEmptyFields() = runBlocking {
        val result = authManager.register("", "", "")
        assertTrue(result is AuthResult.Error)
        assertEquals("Please fill in all required fields", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_REGISTER_004_registrationWithInvalidEmailFormat() = runBlocking {
        val result = authManager.register("Chikondi Banda", "chikondi.com", "Secure@123")
        assertTrue(result is AuthResult.Error)
        assertEquals("Invalid email format", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_LOGIN_004_loginWithEmptyFields() = runBlocking {
        val result = authManager.login("", "")
        assertTrue(result is AuthResult.Error)
        assertEquals("Please fill in all required fields", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_emailValidation() {
        assertFalse(authManager.isValidEmailFormat("chikondi.com"))
        assertTrue(authManager.isValidEmailFormat("chikondi@example.com"))
    }
}
