package com.example.expensetracker

import com.example.expensetracker.transaction.TransactionValidationResult
import com.example.expensetracker.transaction.TransactionValidator
import com.example.expensetracker.viewmodel.TransactionType
import com.example.expensetracker.viewmodel.WalletViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit Tests implementing all Test Cases defined in TC3.pdf (FR-05 through FR-16).
 */
class TransactionManagementUnitTest {

    private lateinit var viewModel: WalletViewModel

    @Before
    fun setUp() {
        viewModel = WalletViewModel()
        viewModel.clearTransactions()
    }

    // ==========================================
    // FR-05: VIEW TRANSACTION HISTORY
    // ==========================================

    @Test
    fun testCase_ET_VIEW_001_viewTransactionHistory() {
        viewModel.addTransaction("Salary", 50000.0, TransactionType.INCOME, "Salary", "01/10/26", "October salary")
        viewModel.addTransaction("Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch")

        val history = viewModel.transactions.value
        assertEquals(2, history.size)
        assertTrue(history.any { it.title == "Salary" && it.amount == 50000.0 })
        assertTrue(history.any { it.title == "Lunch" && it.amount == 5000.0 })
    }

    @Test
    fun testCase_ET_VIEW_002_viewTransactionsWhenNoneExist() {
        viewModel.clearTransactions()
        assertTrue(viewModel.transactions.value.isEmpty())
        assertEquals(0.0, viewModel.totalIncome, 0.01)
        assertEquals(0.0, viewModel.totalExpenses, 0.01)
    }

    // ==========================================
    // FR-06: EDIT TRANSACTION
    // ==========================================

    @Test
    fun testCase_ET_EDIT_001_editTransactionSuccessfully() {
        viewModel.addTransaction("Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch")
        val originalTx = viewModel.transactions.value.first()

        viewModel.updateTransaction(originalTx.id, "Lunch", 6500.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch and drink")

        val updatedTx = viewModel.transactions.value.first { it.id == originalTx.id }
        assertEquals(6500.0, updatedTx.amount, 0.01)
        assertEquals("Lunch and drink", updatedTx.description)
        assertEquals(6500.0, viewModel.totalExpenses, 0.01)
    }

    @Test
    fun testCase_ET_EDIT_002_editTransactionWithEmptyAmount() {
        val result = TransactionValidator.validateTransaction("Lunch", "", TransactionType.EXPENSE, "Food")
        assertTrue(result is TransactionValidationResult.Error)
        assertEquals("Please enter an amount", (result as TransactionValidationResult.Error).message)
    }

    // ==========================================
    // FR-07: DELETE TRANSACTION
    // ==========================================

    @Test
    fun testCase_ET_DELETE_001_deleteTransactionSuccessfully() {
        viewModel.addTransaction("Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch")
        val tx = viewModel.transactions.value.first()

        viewModel.deleteTransaction(tx.id)

        assertTrue(viewModel.transactions.value.none { it.id == tx.id })
        assertEquals(0.0, viewModel.totalExpenses, 0.01)
    }

    @Test
    fun testCase_ET_DELETE_002_cancelTransactionDeletion() {
        viewModel.addTransaction("Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch")
        val count = viewModel.transactions.value.size

        assertEquals(count, viewModel.transactions.value.size)
        assertEquals(5000.0, viewModel.totalExpenses, 0.01)
    }

    // ==========================================
    // FR-08: RECORD EXPENSES UNDER CATEGORIES
    // ==========================================

    @Test
    fun testCase_ET_CATEGORY_001_recordExpensesUnderCategories() {
        viewModel.addTransaction("Food", 3000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.addTransaction("Transport", 2000.0, TransactionType.EXPENSE, "Transport", "02/10/26", "")
        viewModel.addTransaction("Rent", 20000.0, TransactionType.EXPENSE, "Rent", "02/10/26", "")
        viewModel.addTransaction("School", 4000.0, TransactionType.EXPENSE, "School", "02/10/26", "")
        viewModel.addTransaction("Airtime", 1000.0, TransactionType.EXPENSE, "Airtime", "02/10/26", "")
        viewModel.addTransaction("Shopping", 6000.0, TransactionType.EXPENSE, "Shopping", "02/10/26", "")
        viewModel.addTransaction("Entertainment", 2500.0, TransactionType.EXPENSE, "Entertainment", "02/10/26", "")

        val list = viewModel.transactions.value
        assertEquals(7, list.size)
        assertEquals(3000.0, viewModel.getSpentForCategory("Food"), 0.01)
        assertEquals(2000.0, viewModel.getSpentForCategory("Transport"), 0.01)
        assertEquals(20000.0, viewModel.getSpentForCategory("Rent"), 0.01)
        assertEquals(4000.0, viewModel.getSpentForCategory("School"), 0.01)
        assertEquals(1000.0, viewModel.getSpentForCategory("Airtime"), 0.01)
        assertEquals(6000.0, viewModel.getSpentForCategory("Shopping"), 0.01)
        assertEquals(2500.0, viewModel.getSpentForCategory("Entertainment"), 0.01)
    }

    @Test
    fun testCase_ET_CATEGORY_002_saveExpenseWithoutChoosingCategory() {
        val result = TransactionValidator.validateTransaction("Lunch", "5000", TransactionType.EXPENSE, "")
        assertTrue(result is TransactionValidationResult.Error)
        assertEquals("Please select a category", (result as TransactionValidationResult.Error).message)
    }

    // ==========================================
    // FR-09: TOTAL INCOME CALCULATION
    // ==========================================

    @Test
    fun testCase_ET_TOTALINC_001_totalIncomeCalculation() {
        viewModel.addTransaction("Income 1", 50000.0, TransactionType.INCOME, "Salary", "01/10/26", "")
        viewModel.addTransaction("Income 2", 20000.0, TransactionType.INCOME, "Allowance", "01/10/26", "")
        viewModel.addTransaction("Income 3", 5000.0, TransactionType.INCOME, "Gift", "01/10/26", "")

        assertEquals(75000.0, viewModel.totalIncome, 0.01)
    }

    @Test
    fun testCase_ET_TOTALINC_002_totalIncomeWithNoIncomeRecorded() {
        viewModel.clearTransactions()
        assertEquals(0.0, viewModel.totalIncome, 0.01)
    }

    // ==========================================
    // FR-10: TOTAL EXPENSES CALCULATION
    // ==========================================

    @Test
    fun testCase_ET_TOTALEXP_001_totalExpensesCalculation() {
        viewModel.addTransaction("Expense 1", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.addTransaction("Expense 2", 3000.0, TransactionType.EXPENSE, "Transport", "02/10/26", "")
        viewModel.addTransaction("Expense 3", 2000.0, TransactionType.EXPENSE, "Airtime", "02/10/26", "")

        assertEquals(10000.0, viewModel.totalExpenses, 0.01)
    }

    @Test
    fun testCase_ET_TOTALEXP_002_totalExpensesWithNoExpensesRecorded() {
        viewModel.clearTransactions()
        assertEquals(0.0, viewModel.totalExpenses, 0.01)
    }

    // ==========================================
    // FR-11: REMAINING BALANCE CALCULATION
    // ==========================================

    @Test
    fun testCase_ET_BALANCE_001_remainingBalanceCalculation() {
        viewModel.addTransaction("Income", 50000.0, TransactionType.INCOME, "Salary", "01/10/26", "")
        viewModel.addTransaction("Expense", 15000.0, TransactionType.EXPENSE, "Rent", "02/10/26", "")

        assertEquals(35000.0, viewModel.remainingBalance, 0.01)
    }

    @Test
    fun testCase_ET_BALANCE_002_balanceWhenExpensesExceedIncome() {
        viewModel.addTransaction("Income", 10000.0, TransactionType.INCOME, "Allowance", "01/10/26", "")
        viewModel.addTransaction("Expense", 15000.0, TransactionType.EXPENSE, "Rent", "02/10/26", "")

        assertEquals(-5000.0, viewModel.remainingBalance, 0.01)
    }

    @Test
    fun testCase_ET_BALANCE_003_balanceWithNoTransactions() {
        viewModel.clearTransactions()
        assertEquals(0.0, viewModel.remainingBalance, 0.01)
    }

    // ==========================================
    // FR-12 & FR-13: SEARCH AND FILTERING
    // ==========================================

    @Test
    fun testCase_ET_SEARCH_001_searchWithMatchingTerm() {
        viewModel.addTransaction("Campus Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch with study group")
        viewModel.setSearchQuery("Lunch")

        val results = viewModel.currentFilteredTransactions
        assertEquals(1, results.size)
        assertEquals("Campus Lunch", results.first().title)
    }

    @Test
    fun testCase_ET_SEARCH_002_searchWithNoMatchingTerm() {
        viewModel.addTransaction("Campus Lunch", 5000.0, TransactionType.EXPENSE, "Food", "02/10/26", "Lunch")
        viewModel.setSearchQuery("zzzzz")

        val results = viewModel.currentFilteredTransactions
        assertTrue(results.isEmpty())
    }

    @Test
    fun testCase_ET_FILTER_001_filterByCategory() {
        viewModel.addTransaction("Cafeteria", 3000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.addTransaction("Subway Pass", 2000.0, TransactionType.EXPENSE, "Transport", "02/10/26", "")
        viewModel.setCategoryFilter("Food")

        val results = viewModel.currentFilteredTransactions
        assertEquals(1, results.size)
        assertEquals("Food", results.first().category)
    }

    @Test
    fun testCase_ET_FILTER_002_filterByDate() {
        viewModel.addTransaction("Cafeteria", 3000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.setSearchQuery("02/10/26")

        val results = viewModel.currentFilteredTransactions
        assertEquals(1, results.size)
        assertEquals("02/10/26", results.first().date)
    }

    @Test
    fun testCase_ET_FILTER_003_filterWithNoMatchingTransactions() {
        viewModel.addTransaction("Cafeteria", 3000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.setCategoryFilter("Entertainment")

        val results = viewModel.currentFilteredTransactions
        assertTrue(results.isEmpty())
    }

    // ==========================================
    // FR-14: SPENDING SUMMARY BY CATEGORY
    // ==========================================

    @Test
    fun testCase_ET_SUMMARY_001_viewSpendingSummaryByCategory() {
        viewModel.addTransaction("Lunch 1", 3000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.addTransaction("Lunch 2", 2000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")
        viewModel.addTransaction("Bus Ticket", 1500.0, TransactionType.EXPENSE, "Transport", "02/10/26", "")

        assertEquals(5000.0, viewModel.getSpentForCategory("Food"), 0.01)
        assertEquals(1500.0, viewModel.getSpentForCategory("Transport"), 0.01)
    }

    @Test
    fun testCase_ET_SUMMARY_002_viewSpendingSummaryWithNoExpenses() {
        viewModel.clearTransactions()
        assertEquals(0.0, viewModel.getSpentForCategory("Food"), 0.01)
        assertEquals(0.0, viewModel.totalExpenses, 0.01)
    }

    // ==========================================
    // FR-15: CATEGORY BUDGETS
    // ==========================================

    @Test
    fun testCase_ET_BUDGET_001_setCategoryBudget() {
        val success = viewModel.setBudgetLimit("Food", 20000.0)
        assertTrue(success)
        assertEquals(20000.0, viewModel.budgets.value["Food"]!!, 0.01)
    }

    @Test
    fun testCase_ET_BUDGET_002_setBudgetWithInvalidAmount() {
        val success = viewModel.setBudgetLimit("Food", 0.0)
        assertFalse(success)
    }

    // ==========================================
    // FR-16: BUDGET NOTIFICATIONS
    // ==========================================

    @Test
    fun testCase_ET_NOTIFY_001_notificationWhenBudgetIsReached() {
        viewModel.setBudgetLimit("Food", 10000.0)
        viewModel.addTransaction("Cafeteria", 10000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")

        val alerts = viewModel.budgetAlerts
        assertTrue(alerts.any { it.contains("Food") })
    }

    @Test
    fun testCase_ET_NOTIFY_002_notificationWhenBudgetIsExceeded() {
        viewModel.setBudgetLimit("Food", 10000.0)
        viewModel.addTransaction("Cafeteria", 12000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")

        val alerts = viewModel.budgetAlerts
        assertTrue(alerts.any { it.contains("Food") })
    }

    @Test
    fun testCase_ET_NOTIFY_003_noNotificationBelowBudget() {
        viewModel.setBudgetLimit("Food", 10000.0)
        viewModel.addTransaction("Cafeteria", 4000.0, TransactionType.EXPENSE, "Food", "02/10/26", "")

        val foodAlerts = viewModel.budgetAlerts.filter { it.contains("Food") }
        assertTrue(foodAlerts.isEmpty())
    }
}
