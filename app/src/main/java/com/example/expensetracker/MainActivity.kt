package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.expensetracker.ui.*
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.Transaction
import com.example.expensetracker.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    private val walletViewModel: WalletViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                var selectedTab by remember { mutableStateOf(0) }
                var showAddDialog by remember { mutableStateOf(false) }
                var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "Expense Tracker"
                                        1 -> "All Transactions"
                                        else -> "Budget Limits"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Transactions") },
                                label = { Text("Transactions") },
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.PieChart, contentDescription = "Budgets") },
                                label = { Text("Budgets") },
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    indicatorColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> WalletHomeScreen(
                                viewModel = walletViewModel,
                                onNavigateToTransactions = { selectedTab = 1 },
                                onNavigateToBudgets = { selectedTab = 2 },
                                onAddExpenseClick = { showAddDialog = true }
                            )
                            1 -> TransactionsScreen(
                                viewModel = walletViewModel,
                                onEditTransaction = { tx ->
                                    editingTransaction = tx
                                }
                            )
                            2 -> BudgetScreen(
                                viewModel = walletViewModel
                            )
                        }
                    }

                    if (showAddDialog) {
                        AddEditTransactionDialog(
                            onDismiss = { showAddDialog = false },
                            onSave = { title, amount, type, category, date, description ->
                                walletViewModel.addTransaction(title, amount, type, category, date, description)
                            }
                        )
                    }

                    if (editingTransaction != null) {
                        AddEditTransactionDialog(
                            transactionToEdit = editingTransaction,
                            onDismiss = { editingTransaction = null },
                            onSave = { title, amount, type, category, date, description ->
                                walletViewModel.updateTransaction(editingTransaction!!.id, title, amount, type, category, date, description)
                                editingTransaction = null
                            }
                        )
                    }
                }
            }
        }
    }
}
