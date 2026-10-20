package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GroceryItem
import com.example.data.model.Currencies
import com.example.data.model.CurrencyConfig
import com.example.data.model.GroceryCategory
import com.example.data.model.NigerianUnits
import com.example.ui.theme.StatusSuccess

@Composable
fun GroceryItemRow(
    item: GroceryItem,
    currencyCode: String,
    onTogglePurchased: (GroceryItem) -> Unit,
    onAdjustQuantity: (GroceryItem, Double) -> Unit,
    onItemClick: (GroceryItem) -> Unit,
    onChangeCategory: (GroceryItem, GroceryCategory) -> Unit,
    onDeleteItem: (GroceryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val category = item.categoryEnum
    val currency: CurrencyConfig = Currencies.get(currencyCode)
    var showCategoryMenu by remember { mutableStateOf(false) }

    val cardBg by animateColorAsState(
        targetValue = if (item.isPurchased) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 180),
        label = "itemRowBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("grocery_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isPurchased) 0.dp else 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accessible 48dp Tap Checkbox
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onTogglePurchased(item) }
                    .testTag("toggle_item_${item.id}"),
                contentAlignment = Alignment.Center
            ) {
                val circleColor = if (item.isPurchased) {
                    StatusSuccess
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (item.isPurchased) circleColor else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Purchased",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(26.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(2.dp, circleColor)
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Item Name, Category, Price & Note
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onItemClick(item) }
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isPurchased) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Badge with quick dropdown change
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = category.containerColor.copy(alpha = if (item.isPurchased) 0.5f else 0.9f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showCategoryMenu = true }
                                .testTag("category_pill_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = category.emoji, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = category.accentColor
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
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
                                        onChangeCategory(item, cat)
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Unit display
                    val unitAbbr = NigerianUnits.getAbbreviation(item.unit)
                    Text(
                        text = "• ${formatQty(item.quantity)} $unitAbbr",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Price display (Clickable to quickly record actual price paid)
                    val confirmedPrice = item.actualConfirmedLineTotalMinorUnits
                    val estPrice = item.estimatedLineTotalMinorUnits
                    var showQuickPriceDialog by remember { mutableStateOf(false) }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (confirmedPrice != null) StatusSuccess.copy(alpha = 0.1f) else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showQuickPriceDialog = true }
                            .testTag("price_tag_${item.id}")
                    ) {
                        Text(
                            text = when {
                                confirmedPrice != null -> "• Spent: ${currency.formatAmount(confirmedPrice)}"
                                estPrice > 0L -> "• Est: ${currency.formatAmount(estPrice)}"
                                else -> "• + Price"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (confirmedPrice != null) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (confirmedPrice != null) StatusSuccess else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (showQuickPriceDialog) {
                        var inputPrice by remember {
                            mutableStateOf(
                                if (confirmedPrice != null) {
                                    "${confirmedPrice / currency.minorUnitsPerMajor}"
                                } else if (estPrice > 0L) {
                                    "${estPrice / currency.minorUnitsPerMajor}"
                                } else ""
                            )
                        }

                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showQuickPriceDialog = false },
                            title = {
                                Text(
                                    text = "Record Actual Spent",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "Enter actual amount spent for ${item.name} (${formatQty(item.quantity)} ${item.unit}):",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    androidx.compose.material3.OutlinedTextField(
                                        value = inputPrice,
                                        onValueChange = { inputPrice = it },
                                        label = { Text("Amount (${currency.symbol})") },
                                        placeholder = { Text("e.g. 2500") },
                                        singleLine = true,
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("quick_price_input")
                                    )
                                }
                            },
                            confirmButton = {
                                androidx.compose.material3.Button(
                                    onClick = {
                                        val minor = currency.parseToMinorUnits(inputPrice)
                                        if (minor != null) {
                                            onItemClick(
                                                item.copy(
                                                    actualLineTotalMinorUnits = minor,
                                                    useActualLineTotal = true,
                                                    isPurchased = true
                                                )
                                            )
                                        }
                                        showQuickPriceDialog = false
                                    }
                                ) {
                                    Text("Save Spending")
                                }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(onClick = { showQuickPriceDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }

                if (item.notes.isNotBlank()) {
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Quantity adjusters (+ / -) when not purchased
            if (!item.isPurchased) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onAdjustQuantity(item, -1.0) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("decrease_qty_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = formatQty(item.quantity),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { onAdjustQuantity(item, 1.0) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("increase_qty_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Edit / Details button
            IconButton(
                onClick = { onItemClick(item) },
                modifier = Modifier
                    .size(38.dp)
                    .testTag("edit_item_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${item.name}",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatQty(qty: Double): String {
    return if (qty % 1.0 == 0.0) {
        qty.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", qty)
    }
}
