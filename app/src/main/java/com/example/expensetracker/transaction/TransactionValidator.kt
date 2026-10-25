package com.example.expensetracker.transaction

import com.example.expensetracker.viewmodel.TransactionType

sealed class TransactionValidationResult {
    data class Success(val amount: Double) : TransactionValidationResult()
    data class Error(val message: String) : TransactionValidationResult()
}

object TransactionValidator {

    /**
     * Filters out symbols, letters (e.g. "abc", "zzzz", "@#$%") and denies non-numeric characters.
     */
    fun sanitizeAmountInput(rawInput: String): String {
        val filtered = rawInput.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } > 1) {
            val firstDot = filtered.indexOf('.')
            return filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
        }
        return filtered
    }

    /**
     * Validates transaction input fields according to TC2.pdf test requirements.
     */
    fun validateTransaction(
        title: String,
        amountRaw: String,
        type: TransactionType,
        category: String
    ): TransactionValidationResult {
        // ET_INCOME_002: Add Income with Empty Amount
        if (amountRaw.isBlank()) {
            return TransactionValidationResult.Error("Please enter an amount")
        }

        // ET_EXPENSE_003 / Additional: Non-numeric / letters / symbols
        val parsedAmount = amountRaw.toDoubleOrNull()
        if (parsedAmount == null) {
            return TransactionValidationResult.Error("Please enter a valid numeric amount")
        }

        // ET_INCOME_003: Add Income with Zero Amount
        if (parsedAmount <= 0.0) {
            return TransactionValidationResult.Error("Amount must be greater than zero")
        }

        // ET_EXPENSE_002: Add Expense Without Category
        if (category.isBlank() || category.equals("none", ignoreCase = true)) {
            return TransactionValidationResult.Error("Please select a category")
        }

        if (title.isBlank()) {
            return TransactionValidationResult.Error("Please enter a title or source")
        }

        return TransactionValidationResult.Success(parsedAmount)
    }
}
