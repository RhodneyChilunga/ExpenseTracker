package com.example.expensetracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.ui.AddEditTransactionDialog
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.TransactionType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var savedTitle = ""
    private var savedAmount = 0.0
    private var savedType = TransactionType.EXPENSE
    private var savedCategory = ""

    @Before
    fun setUp() {
        savedTitle = ""
        savedAmount = 0.0
        savedType = TransactionType.EXPENSE
        savedCategory = ""

        composeTestRule.setContent {
            ExpenseTrackerTheme {
                AddEditTransactionDialog(
                    onDismiss = {},
                    onSave = { title, amount, type, category, _, _ ->
                        savedTitle = title
                        savedAmount = amount
                        savedType = type
                        savedCategory = category
                    }
                )
            }
        }
    }

    @Test
    fun testCase_ET_EXPENSE_001_addExpenseSuccessfully() {
        composeTestRule.onNodeWithText("Merchant / Title").performTextInput("Campus Lunch")
        composeTestRule.onNodeWithText("Amount (MWK)").performTextInput("5000")
        composeTestRule.onNodeWithText("Save").performClick()

        assert(savedTitle == "Campus Lunch")
        assert(savedAmount == 5000.0)
        assert(savedType == TransactionType.EXPENSE)
        assert(savedCategory == "Food")
    }

    @Test
    fun testCase_ET_INCOME_001_addIncomeSuccessfully() {
        composeTestRule.onNodeWithText("Income").performClick()
        composeTestRule.onNodeWithText("Income Source").performTextInput("October salary")
        composeTestRule.onNodeWithText("Amount (MWK)").performTextInput("50000")
        composeTestRule.onNodeWithText("Save").performClick()

        assert(savedTitle == "October salary")
        assert(savedAmount == 50000.0)
        assert(savedType == TransactionType.INCOME)
        assert(savedCategory == "Allowance")
    }

    @Test
    fun testCase_ET_INCOME_002_addIncomeWithEmptyAmount() {
        composeTestRule.onNodeWithText("Income").performClick()
        composeTestRule.onNodeWithText("Income Source").performTextInput("October salary")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Please enter an amount").assertIsDisplayed()
        assert(savedAmount == 0.0)
    }

    @Test
    fun testCase_ET_INCOME_003_addIncomeWithZeroAmount() {
        composeTestRule.onNodeWithText("Income").performClick()
        composeTestRule.onNodeWithText("Income Source").performTextInput("October salary")
        composeTestRule.onNodeWithText("Amount (MWK)").performTextInput("0")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Amount must be greater than zero").assertIsDisplayed()
        assert(savedAmount == 0.0)
    }

    @Test
    fun testCase_ET_EXPENSE_003_addExpenseWithNonNumericAmount() {
        composeTestRule.onNodeWithText("Merchant / Title").performTextInput("Lunch")
        composeTestRule.onNodeWithText("Amount (MWK)").performTextInput("abc")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Please enter a valid numeric amount").assertIsDisplayed()
        assert(savedAmount == 0.0)
    }
}
