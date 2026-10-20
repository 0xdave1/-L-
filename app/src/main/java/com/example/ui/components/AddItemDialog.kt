package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.classifier.CategoryClassifier
import com.example.data.model.Currencies
import com.example.data.model.CurrencyConfig
import com.example.data.model.GroceryCategory
import com.example.data.model.NigerianUnits

@Composable
fun AddItemDialog(
    initialName: String = "",
    currencyCode: String = "NGN",
    onConfirm: (
        name: String,
        quantity: Double,
        unit: String,
        estimatedPriceMinorUnits: Long?,
        category: GroceryCategory,
        notes: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val currency: CurrencyConfig = Currencies.get(currencyCode)

    var name by remember { mutableStateOf(initialName) }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember {
        mutableStateOf(
            if (initialName.isNotBlank()) CategoryClassifier.suggestUnit(initialName) else NigerianUnits.PIECE
        )
    }

    var selectedCategory by remember {
        mutableStateOf(
            if (initialName.isNotBlank()) CategoryClassifier.classify(initialName) else GroceryCategory.OTHER
        )
    }
    var manualCategorySelected by remember { mutableStateOf(false) }

    var estimatedPriceText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showUnitDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Grocery Item",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (!manualCategorySelected && it.isNotBlank()) {
                            selectedCategory = CategoryClassifier.classify(it)
                            unit = CategoryClassifier.suggestUnit(it)
                        }
                    },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Rice, Garri, Tomatoes, Milk") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quantity and Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_dialog_qty_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Unit Dropdown
                    Box(modifier = Modifier.weight(1.3f)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showUnitDropdown = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = unit,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Unit",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showUnitDropdown,
                            onDismissRequest = { showUnitDropdown = false }
                        ) {
                            NigerianUnits.standardList.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        unit = u
                                        showUnitDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Auto Category Badge & Picker
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = selectedCategory.containerColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedCategory.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = selectedCategory.displayName,
                                        fontWeight = FontWeight.Bold,
                                        color = selectedCategory.accentColor
                                    )
                                    Text(
                                        text = if (manualCategorySelected) "Manual Category" else "Auto-Assigned Category",
                                        fontSize = 10.sp,
                                        color = selectedCategory.accentColor.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change Category",
                                tint = selectedCategory.accentColor
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        GroceryCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(cat.emoji, modifier = Modifier.padding(end = 8.dp))
                                        Text(cat.displayName)
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    manualCategorySelected = true
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Estimated Unit Price
                OutlinedTextField(
                    value = estimatedPriceText,
                    onValueChange = { estimatedPriceText = it },
                    label = { Text("Est. Unit Price (${currency.symbol})") },
                    placeholder = { Text("e.g. 2500") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_est_price_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("e.g. Fresh red ones, Derby brand") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQty = quantityText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0
                    val estPriceMinor = currency.parseToMinorUnits(estimatedPriceText)
                    onConfirm(
                        name.trim(),
                        parsedQty,
                        unit,
                        estPriceMinor,
                        selectedCategory,
                        notes.trim()
                    )
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("add_dialog_confirm_button")
            ) {
                Text("Add to List")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_dialog_cancel_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("add_item_dialog")
    )
}
