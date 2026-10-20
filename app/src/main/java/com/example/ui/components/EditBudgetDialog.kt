package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ShoppingList
import com.example.data.model.Currencies
import com.example.data.model.CurrencyConfig

@Composable
fun EditBudgetDialog(
    list: ShoppingList,
    onConfirm: (budgetMinorUnits: Long?, currencyCode: String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialCurrency = Currencies.get(list.currencyCode)
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }
    var budgetText by remember {
        mutableStateOf(
            if (list.budgetMinorUnits != null) {
                val major = list.budgetMinorUnits / initialCurrency.minorUnitsPerMajor
                "$major"
            } else ""
        )
    }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Set Shopping Budget",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Track your market expenses and avoid overspending for \"${list.name}\".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clickable { showCurrencyDropdown = true }
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "${selectedCurrency.symbol} (${selectedCurrency.code})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showCurrencyDropdown,
                            onDismissRequest = { showCurrencyDropdown = false }
                        ) {
                            Currencies.supported.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text("${curr.symbol} - ${curr.name} (${curr.code})") },
                                    onClick = {
                                        selectedCurrency = curr
                                        showCurrencyDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = budgetText,
                        onValueChange = { budgetText = it },
                        label = { Text("Budget Limit") },
                        placeholder = { Text("e.g. 50000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("budget_limit_input")
                    )
                }

                if (budgetText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { budgetText = "" },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Remove Budget Limit", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budgetMinor = selectedCurrency.parseToMinorUnits(budgetText)
                    onConfirm(budgetMinor, selectedCurrency.code)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_budget_btn")
            ) {
                Text("Save Budget")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_budget_btn")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("edit_budget_dialog")
    )
}
