package com.example.expensetracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.auth.AuthManager
import com.example.expensetracker.ui.LoginScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authManager: AuthManager
    private var loginSuccessful = false

    @Before
    fun setUp() {
        authManager = AuthManager()
        authManager.resetUsers()
        loginSuccessful = false

        composeTestRule.setContent {
            ExpenseTrackerTheme {
                LoginScreen(
                    authManager = authManager,
                    onLoginSuccess = { loginSuccessful = true }
                )
            }
        }
    }

    @Test
    fun testCase_ET_LOGIN_001_successfulLogin() {
        composeTestRule.onNodeWithText("Enter your email").performTextInput("chikondi@example.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("Secure@123")
        composeTestRule.onNodeWithText("Login").performClick()

        assert(loginSuccessful)
    }

    @Test
    fun testCase_ET_LOGIN_002_loginWithIncorrectPassword() {
        composeTestRule.onNodeWithText("Enter your email").performTextInput("chikondi@example.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("WrongPass")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Email or password is incorrect").assertIsDisplayed()
        assert(!loginSuccessful)
    }

    @Test
    fun testCase_ET_LOGIN_003_loginWithUnregisteredEmail() {
        composeTestRule.onNodeWithText("Enter your email").performTextInput("unknown@example.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("Secure@123")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Email or password is incorrect").assertIsDisplayed()
        assert(!loginSuccessful)
    }

    @Test
    fun testCase_ET_LOGIN_004_loginWithEmptyFields() {
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Please fill in all required fields").assertIsDisplayed()
        assert(!loginSuccessful)
    }

    @Test
    fun testCase_ET_REGISTER_001_successfulRegistration() {
        composeTestRule.onNodeWithText("Sign Up").performClick()

        composeTestRule.onNodeWithText("Enter your username").performTextInput("Chikondi Banda")
        composeTestRule.onNodeWithText("Enter your email").performTextInput("new_user@example.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("Secure@123")
        composeTestRule.onNodeWithText("Confirm your password").performTextInput("Secure@123")

        composeTestRule.onAllNodesWithText("Sign Up")[0].performClick()

        composeTestRule.onNodeWithText("Account registered successfully").assertIsDisplayed()
    }

    @Test
    fun testCase_ET_REGISTER_002_registrationWithExistingEmail() {
        composeTestRule.onNodeWithText("Sign Up").performClick()

        composeTestRule.onNodeWithText("Enter your username").performTextInput("Chikondi Banda")
        composeTestRule.onNodeWithText("Enter your email").performTextInput("chikondi@example.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("Secure@123")

        composeTestRule.onAllNodesWithText("Sign Up")[0].performClick()

        composeTestRule.onNodeWithText("Email is already registered").assertIsDisplayed()
    }

    @Test
    fun testCase_ET_REGISTER_004_registrationWithInvalidEmailFormat() {
        composeTestRule.onNodeWithText("Sign Up").performClick()

        composeTestRule.onNodeWithText("Enter your username").performTextInput("Chikondi Banda")
        composeTestRule.onNodeWithText("Enter your email").performTextInput("chikondi.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("Secure@123")

        composeTestRule.onAllNodesWithText("Sign Up")[0].performClick()

        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
    }
}
