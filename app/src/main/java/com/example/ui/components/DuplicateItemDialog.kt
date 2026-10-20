package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.DuplicatePromptData

@Composable
fun DuplicateItemDialog(
    data: DuplicatePromptData,
    onIncreaseQuantity: () -> Unit,
    onKeepSeparate: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAlreadyPurchased = data.existingItem.isPurchased

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Item Already on List",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "\"${data.candidateName}\" is already present in this shopping list (${data.existingItem.quantity} ${data.existingItem.unit}).",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isAlreadyPurchased) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: This item was already ticked off as purchased. Increasing its quantity will move it back to your active shopping list so you can purchase more.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Would you like to increase the existing item's quantity or keep a separate line item?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onIncreaseQuantity,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("duplicate_increase_btn")
            ) {
                Text("Increase Quantity")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onKeepSeparate,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("duplicate_keep_separate_btn")
            ) {
                Text("Keep Separate")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("duplicate_item_dialog")
    )
}
