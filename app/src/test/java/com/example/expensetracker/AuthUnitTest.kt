package com.example.expensetracker

import com.example.expensetracker.auth.AuthManager
import com.example.expensetracker.auth.AuthResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit Tests implementing the 8 Test Cases defined in TC1.pdf (FR-01 and FR-02).
 */
class AuthUnitTest {

    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        authManager = AuthManager()
        authManager.resetUsers()
    }

    // ==========================================
    // FR-01: REGISTRATION TEST CASES
    // ==========================================

    @Test
    fun testCase_ET_REGISTER_001_successfulAccountRegistration() {
        // Description: Verify that a new user can create an account using valid registration details.
        val name = "Chikondi Banda"
        val email = "chikondi_new@example.com"
        val password = "Secure@123"

        val result = authManager.register(name, email, password)

        assertTrue("Expected AuthResult.Success for valid registration", result is AuthResult.Success)
        assertEquals("Account registered successfully", (result as AuthResult.Success).message)
        assertTrue("Newly registered email should exist in system", authManager.isEmailRegistered(email))

        // Post-condition check: Can be used to log in
        val loginResult = authManager.login(email, password)
        assertTrue("User should be able to log in with new credentials", loginResult is AuthResult.Success)
    }

    @Test
    fun testCase_ET_REGISTER_002_registrationWithExistingEmail() {
        // Description: Verify that registration fails when the email is already registered.
        val name = "Chikondi Banda"
        val existingEmail = "chikondi@example.com" // Pre-registered in prerequisites
        val password = "Secure@123"

        val result = authManager.register(name, existingEmail, password)

        assertTrue("Expected AuthResult.Error when registering with existing email", result is AuthResult.Error)
        assertEquals("Email is already registered", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_REGISTER_003_registrationWithEmptyFields() {
        // Description: Verify that registration is blocked when required fields are left empty.
        val name = ""
        val email = ""
        val password = ""

        val result = authManager.register(name, email, password)

        assertTrue("Expected AuthResult.Error when fields are empty", result is AuthResult.Error)
        assertEquals("Please fill in all required fields", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_REGISTER_004_registrationWithInvalidEmailFormat() {
        // Description: Verify that registration is blocked when the email format is invalid.
        val name = "Chikondi Banda"
        val invalidEmail = "chikondi.com"
        val password = "Secure@123"

        val result = authManager.register(name, invalidEmail, password)

        assertTrue("Expected AuthResult.Error for invalid email format", result is AuthResult.Error)
        assertEquals("Invalid email format", (result as AuthResult.Error).message)
    }

    // ==========================================
    // FR-02: LOGIN TEST CASES
    // ==========================================

    @Test
    fun testCase_ET_LOGIN_001_successfulLogin() {
        // Description: Verify that a registered user can log in with a valid email and password.
        val email = "chikondi@example.com"
        val password = "Secure@123"

        val result = authManager.login(email, password)

        assertTrue("Expected AuthResult.Success for valid login credentials", result is AuthResult.Success)
        assertEquals("Login successful", (result as AuthResult.Success).message)
    }

    @Test
    fun testCase_ET_LOGIN_002_loginWithIncorrectPassword() {
        // Description: Verify that login fails when a registered email is entered with an incorrect password.
        val email = "chikondi@example.com"
        val wrongPassword = "WrongPass"

        val result = authManager.login(email, wrongPassword)

        assertTrue("Expected AuthResult.Error for incorrect password", result is AuthResult.Error)
        assertEquals("Email or password is incorrect", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_LOGIN_003_loginWithUnregisteredEmail() {
        // Description: Verify that login fails when an email that is not registered is entered.
        val unregisteredEmail = "unknown@example.com"
        val password = "Secure@123"

        val result = authManager.login(unregisteredEmail, password)

        assertTrue("Expected AuthResult.Error for unregistered email", result is AuthResult.Error)
        assertEquals("Email or password is incorrect", (result as AuthResult.Error).message)
    }

    @Test
    fun testCase_ET_LOGIN_004_loginWithEmptyFields() {
        // Description: Verify that login is blocked when both fields are empty.
        val emptyEmail = ""
        val emptyPassword = ""

        val result = authManager.login(emptyEmail, emptyPassword)

        assertTrue("Expected AuthResult.Error when login fields are empty", result is AuthResult.Error)
        assertEquals("Please fill in all required fields", (result as AuthResult.Error).message)
    }
}
