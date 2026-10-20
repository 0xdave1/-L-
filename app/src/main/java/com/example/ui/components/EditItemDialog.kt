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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.example.data.entity.GroceryItem
import com.example.data.model.Currencies
import com.example.data.model.CurrencyConfig
import com.example.data.model.GroceryCategory
import com.example.data.model.NigerianUnits
import com.example.ui.theme.StatusError

@Composable
fun EditItemDialog(
    item: GroceryItem,
    currencyCode: String,
    onSave: (GroceryItem) -> Unit,
    onDelete: (GroceryItem) -> Unit,
    onDismiss: () -> Unit
) {
    val currency: CurrencyConfig = Currencies.get(currencyCode)

    var name by remember { mutableStateOf(item.name) }
    var quantityText by remember { mutableStateOf(if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString()) }
    var unit by remember { mutableStateOf(item.unit) }
    var selectedCategory by remember { mutableStateOf(item.categoryEnum) }
    var estimatedPriceText by remember {
        mutableStateOf(
            if (item.estimatedUnitPriceMinorUnits != null) {
                val major = item.estimatedUnitPriceMinorUnits / currency.minorUnitsPerMajor
                "$major"
            } else ""
        )
    }
    var actualPriceText by remember {
        mutableStateOf(
            if (item.actualUnitPriceMinorUnits != null) {
                val major = item.actualUnitPriceMinorUnits / currency.minorUnitsPerMajor
                "$major"
            } else ""
        )
    }
    var actualLineTotalText by remember {
        mutableStateOf(
            if (item.actualLineTotalMinorUnits != null) {
                val major = item.actualLineTotalMinorUnits / currency.minorUnitsPerMajor
                "$major"
            } else ""
        )
    }
    var useActualLineTotal by remember { mutableStateOf(item.useActualLineTotal) }
    var notes by remember { mutableStateOf(item.notes) }

    var showUnitDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Grocery Item",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
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
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_item_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quantity & Unit Selection
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
                        modifier = Modifier.weight(1f).testTag("edit_item_qty")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Unit Dropdown
                    Box(modifier = Modifier.weight(1.2f)) {
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
                                    contentDescription = "Select unit",
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

                // Category selector
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
                                Text(
                                    text = selectedCategory.displayName,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedCategory.accentColor
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change category",
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
                    placeholder = { Text("e.g. 1500") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_item_est_price")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle between Actual Unit Price or Actual Line Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (useActualLineTotal) "Enter Actual Total" else "Enter Actual Unit Price",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = useActualLineTotal,
                        onCheckedChange = { useActualLineTotal = it }
                    )
                }

                if (useActualLineTotal) {
                    OutlinedTextField(
                        value = actualLineTotalText,
                        onValueChange = { actualLineTotalText = it },
                        label = { Text("Actual Line Total (${currency.symbol})") },
                        placeholder = { Text("e.g. 4500") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_item_act_total")
                    )
                } else {
                    OutlinedTextField(
                        value = actualPriceText,
                        onValueChange = { actualPriceText = it },
                        label = { Text("Actual Unit Price (${currency.symbol})") },
                        placeholder = { Text("e.g. 1600") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_item_act_price")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. Derica brand, soft)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_item_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQty = quantityText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0
                    val estPriceMinor = currency.parseToMinorUnits(estimatedPriceText)
                    val actPriceMinor = currency.parseToMinorUnits(actualPriceText)
                    val actTotalMinor = currency.parseToMinorUnits(actualLineTotalText)

                    val updated = item.copy(
                        name = name.trim().ifBlank { item.name },
                        normalizedName = name.trim().lowercase().ifBlank { item.normalizedName },
                        quantity = parsedQty,
                        unit = unit,
                        categoryId = selectedCategory.id,
                        estimatedUnitPriceMinorUnits = estPriceMinor,
                        actualUnitPriceMinorUnits = actPriceMinor,
                        actualLineTotalMinorUnits = actTotalMinor,
                        useActualLineTotal = useActualLineTotal,
                        notes = notes.trim()
                    )
                    onSave(updated)
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_item_details_btn")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDelete(item) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                modifier = Modifier.testTag("delete_item_from_dialog_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Delete")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("edit_item_dialog")
    )
}
