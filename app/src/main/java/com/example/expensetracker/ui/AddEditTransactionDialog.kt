package com.example.expensetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.transaction.TransactionValidationResult
import com.example.expensetracker.transaction.TransactionValidator
import com.example.expensetracker.viewmodel.Transaction
import com.example.expensetracker.viewmodel.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
    transactionToEdit: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, type: TransactionType, category: String, date: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amountText by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(transactionToEdit?.category ?: "Food") }
    var date by remember { mutableStateOf(transactionToEdit?.date ?: "2025-10-25") }
    var description by remember { mutableStateOf(transactionToEdit?.description ?: "") }

    var expandedCategory by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val expenseCategories = listOf("Food", "Transport", "Rent", "School", "Airtime", "Shopping", "Entertainment", "Other")
    val incomeCategories = listOf("Allowance", "Salary", "Stipend", "Gift", "Freelance", "Other")

    val categories = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (transactionToEdit == null) "Add Transaction" else "Edit Transaction",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type Selector (Income / Expense)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            type = TransactionType.EXPENSE
                            category = "Food"
                            errorMessage = null
                        },
                        label = {
                            Text(
                                text = "Expense",
                                fontWeight = if (type == TransactionType.EXPENSE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = type == TransactionType.EXPENSE,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = {
                            type = TransactionType.INCOME
                            category = "Allowance"
                            errorMessage = null
                        },
                        label = {
                            Text(
                                text = "Income",
                                fontWeight = if (type == TransactionType.INCOME) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = type == TransactionType.INCOME,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Error Message Display
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text(if (type == TransactionType.EXPENSE) "Merchant / Title" else "Income Source") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { raw ->
                        amountText = raw
                        errorMessage = null
                    },
                    label = { Text("Amount (MWK)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                    errorMessage = null
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        errorMessage = null
                    },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val result = TransactionValidator.validateTransaction(title, amountText, type, category)
                    when (result) {
                        is TransactionValidationResult.Success -> {
                            onSave(title, result.amount, type, category, date, description)
                            onDismiss()
                        }
                        is TransactionValidationResult.Error -> {
                            errorMessage = result.message
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
