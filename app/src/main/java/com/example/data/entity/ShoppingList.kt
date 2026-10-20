package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val shoppingDate: Long? = null,
    val budgetMinorUnits: Long? = null,
    val currencyCode: String = "NGN",
    val status: String = STATUS_ACTIVE
) {
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_ARCHIVED = "ARCHIVED"
    }

    val isCompleted: Boolean
        get() = status == STATUS_COMPLETED
}
