package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Locale
import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String, // Source for income, Title/Merchant for expense
    val amount: Double,
    val type: TransactionType,
    val category: String, // Food, Transport, Rent, School, Airtime, Shopping, Entertainment, Allowance, Salary, Other
    val date: String,
    val description: String = ""
)

class WalletViewModel : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(
        listOf(
            Transaction(title = "Allowance / Stipend", amount = 400.0, type = TransactionType.INCOME, category = "Allowance", date = "2025-10-01", description = "Monthly parent allowance"),
            Transaction(title = "Calculus Textbook", amount = 45.0, type = TransactionType.EXPENSE, category = "School", date = "2025-10-02", description = "Semester 4 book"),
            Transaction(title = "Campus Cafeteria Lunch", amount = 8.5, type = TransactionType.EXPENSE, category = "Food", date = "2025-10-03", description = "Lunch with study group"),
            Transaction(title = "Monthly Rent Share", amount = 150.0, type = TransactionType.EXPENSE, category = "Rent", date = "2025-10-01", description = "Student apartment rent"),
            Transaction(title = "Subway Monthly Pass", amount = 30.0, type = TransactionType.EXPENSE, category = "Transport", date = "2025-10-04", description = "Student transit pass"),
            Transaction(title = "Mobile Airtime & Data", amount = 15.0, type = TransactionType.EXPENSE, category = "Airtime", date = "2025-10-05", description = "Monthly 10GB bundle"),
            Transaction(title = "Weekend Movie Night", amount = 18.25, type = TransactionType.EXPENSE, category = "Entertainment", date = "2025-10-06", description = "Cinema tickets")
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _budgets = MutableStateFlow<Map<String, Double>>(
        mapOf(
            "Food" to 100.0,
            "School" to 60.0,
            "Transport" to 40.0,
            "Rent" to 150.0,
            "Airtime" to 20.0,
            "Shopping" to 50.0,
            "Entertainment" to 30.0
        )
    )
    val budgets: StateFlow<Map<String, Double>> = _budgets.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    val totalIncome: Double
        get() = _transactions.value.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

    val totalExpenses: Double
        get() = _transactions.value.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val remainingBalance: Double
        get() = totalIncome - totalExpenses

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        _transactions,
        _searchQuery,
        _selectedCategoryFilter
    ) { txList, queryStr, categoryFilter ->
        val query = queryStr.trim().lowercase()
        txList.filter { tx ->
            val matchesQuery = query.isEmpty() ||
                    tx.title.lowercase().contains(query) ||
                    tx.category.lowercase().contains(query) ||
                    tx.description.lowercase().contains(query) ||
                    tx.date.lowercase().contains(query) ||
                    tx.amount.toString().contains(query) ||
                    String.format(Locale.getDefault(), "%.2f", tx.amount).contains(query)

            if (query.isNotEmpty()) {
                val queryIsCategory = txList.any { it.category.equals(query, ignoreCase = true) }
                if (queryIsCategory) {
                    matchesQuery
                } else if (categoryFilter != null) {
                    matchesQuery && tx.category.equals(categoryFilter, ignoreCase = true)
                } else {
                    matchesQuery
                }
            } else {
                categoryFilter == null || tx.category.equals(categoryFilter, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _transactions.value
    )

    val budgetAlerts: List<String>
        get() {
            val alerts = mutableListOf<String>()
            val expenseMap = _transactions.value
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            for ((cat, limit) in _budgets.value) {
                val spent = expenseMap[cat] ?: 0.0
                if (spent >= limit) {
                    alerts.add("⚠️ Budget exceeded or reached for $cat! Spent: MWK $spent / Limit: MWK $limit")
                } else if (spent >= limit * 0.8) {
                    alerts.add("⚡ Near budget limit for $cat (MWK $spent / MWK $limit)")
                }
            }
            return alerts
        }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String,
        description: String
    ) {
        val newTx = Transaction(
            title = title,
            amount = amount,
            type = type,
            category = category,
            date = date,
            description = description
        )
        _transactions.update { listOf(newTx) + it }
    }

    fun updateTransaction(
        id: String,
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String,
        description: String
    ) {
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id == id) {
                    tx.copy(
                        title = title,
                        amount = amount,
                        type = type,
                        category = category,
                        date = date,
                        description = description
                    )
                } else {
                    tx
                }
            }
        }
    }

    fun deleteTransaction(id: String) {
        _transactions.update { list -> list.filter { it.id != id } }
    }

    fun setBudgetLimit(category: String, limit: Double) {
        _budgets.update { map ->
            map + (category to limit)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun getSpentForCategory(category: String): Double {
        return _transactions.value
            .filter { it.type == TransactionType.EXPENSE && it.category == category }
            .sumOf { it.amount }
    }
}
