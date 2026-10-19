package com.example.expensetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.viewmodel.WalletViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: WalletViewModel
) {
    val budgets by viewModel.budgets.collectAsState()
    var editingCategory by remember { mutableStateOf<String?>(null) }
    var newLimitText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Category Budget Limits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Set spending limits per category. You will receive notifications when you reach or exceed your set budget.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(budgets.toList()) { (category, limit) ->
            val spent = viewModel.getSpentForCategory(category)
            val progress = (spent / limit).toFloat().coerceIn(0f, 1f)
            val isExceeded = spent >= limit
            val isNearLimit = spent >= limit * 0.8 && !isExceeded

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = {
                                editingCategory = category
                                newLimitText = limit.toString()
                            }
                        ) {
                            Text("Edit Limit")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            isExceeded -> MaterialTheme.colorScheme.error
                            isNearLimit -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Spent: MWK " + String.format(Locale.getDefault(), "%.2f", spent),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Limit: MWK " + String.format(Locale.getDefault(), "%.2f", limit),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isExceeded) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ Budget limit exceeded!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (isNearLimit) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚡ Near budget limit (80%+)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (editingCategory != null) {
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Set Budget Limit for $editingCategory") },
            text = {
                OutlinedTextField(
                    value = newLimitText,
                    onValueChange = { newLimitText = it },
                    label = { Text("New Limit (MWK)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = newLimitText.toDoubleOrNull()
                        if (newLimit != null && newLimit > 0) {
                            viewModel.setBudgetLimit(editingCategory!!, newLimit)
                            editingCategory = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
