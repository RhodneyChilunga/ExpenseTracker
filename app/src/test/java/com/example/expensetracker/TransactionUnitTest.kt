package com.example.expensetracker

import com.example.expensetracker.transaction.TransactionValidationResult
import com.example.expensetracker.transaction.TransactionValidator
import com.example.expensetracker.viewmodel.TransactionType
import com.example.expensetracker.viewmodel.WalletViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit Tests implementing the Test Cases defined in TC2.pdf (FR-03 & FR-04).
 */
class TransactionUnitTest {

    private lateinit var viewModel: WalletViewModel

    @Before
    fun setUp() {
        viewModel = WalletViewModel()
    }

    // ==========================================
    // FR-03: ADD INCOME TEST CASES
    // ==========================================

    @Test
    fun testCase_ET_INCOME_001_addIncomeSuccessfully() {
        // Description: Verify that a logged-in user can add an income transaction with valid details.
        val amountRaw = "50000"
        val source = "Salary"
        val date = "01/10/26"
        val description = "October salary"

        val result = TransactionValidator.validateTransaction(source, amountRaw, TransactionType.INCOME, "Salary")
        assertTrue("Expected validation success for valid income details", result is TransactionValidationResult.Success)
        assertEquals(50000.0, (result as TransactionValidationResult.Success).amount, 0.01)

        // Add to ViewModel and verify inclusion in total income
        val initialIncome = viewModel.totalIncome
        viewModel.addTransaction(source, result.amount, TransactionType.INCOME, "Salary", date, description)
        assertEquals(initialIncome + 50000.0, viewModel.totalIncome, 0.01)
    }

    @Test
    fun testCase_ET_INCOME_002_addIncomeWithEmptyAmount() {
        // Description: Verify that an income transaction cannot be saved without an amount.
        val amountRaw = ""
        val source = "Salary"

        val result = TransactionValidator.validateTransaction(source, amountRaw, TransactionType.INCOME, "Salary")
        assertTrue("Expected error for empty income amount", result is TransactionValidationResult.Error)
        assertEquals("Please enter an amount", (result as TransactionValidationResult.Error).message)
    }

    @Test
    fun testCase_ET_INCOME_003_addIncomeWithZeroAmount() {
        // Description: Verify that an income transaction with an amount of zero is rejected.
        val amountRaw = "0"
        val source = "Salary"

        val result = TransactionValidator.validateTransaction(source, amountRaw, TransactionType.INCOME, "Salary")
        assertTrue("Expected error for zero income amount", result is TransactionValidationResult.Error)
        assertEquals("Amount must be greater than zero", (result as TransactionValidationResult.Error).message)
    }

    // ==========================================
    // FR-04: ADD EXPENSE TEST CASES
    // ==========================================

    @Test
    fun testCase_ET_EXPENSE_001_addExpenseSuccessfully() {
        // Description: Verify that a logged-in user can add an expense transaction with valid details.
        val amountRaw = "5000"
        val category = "Food"
        val date = "02/10/26"
        val description = "Lunch"

        val result = TransactionValidator.validateTransaction("Campus Lunch", amountRaw, TransactionType.EXPENSE, category)
        assertTrue("Expected validation success for valid expense details", result is TransactionValidationResult.Success)
        assertEquals(5000.0, (result as TransactionValidationResult.Success).amount, 0.01)

        val initialExpenses = viewModel.totalExpenses
        viewModel.addTransaction("Campus Lunch", result.amount, TransactionType.EXPENSE, category, date, description)
        assertEquals(initialExpenses + 5000.0, viewModel.totalExpenses, 0.01)
    }

    @Test
    fun testCase_ET_EXPENSE_002_addExpenseWithoutCategory() {
        // Description: Verify that an expense cannot be saved when no category is selected.
        val amountRaw = "5000"
        val category = "none"

        val result = TransactionValidator.validateTransaction("Lunch", amountRaw, TransactionType.EXPENSE, category)
        assertTrue("Expected error when no category is selected", result is TransactionValidationResult.Error)
        assertEquals("Please select a category", (result as TransactionValidationResult.Error).message)
    }

    @Test
    fun testCase_ET_EXPENSE_003_addExpenseWithNonNumericAmount() {
        // Description: Verify that an expense with a non-numeric amount is rejected.
        val amountRaw = "abc"

        val result = TransactionValidator.validateTransaction("Lunch", amountRaw, TransactionType.EXPENSE, "Food")
        assertTrue("Expected error for non-numeric amount", result is TransactionValidationResult.Error)
        assertEquals("Please enter a valid numeric amount", (result as TransactionValidationResult.Error).message)
    }
}
