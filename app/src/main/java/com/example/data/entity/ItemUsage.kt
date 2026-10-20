package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.GroceryCategory

@Entity(
    tableName = "item_usage",
    indices = [Index(value = ["normalizedItemName"], unique = true)]
)
data class ItemUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val normalizedItemName: String,
    val displayName: String,
    val categoryId: String = GroceryCategory.OTHER.id,
    val timesAdded: Int = 1,
    val timesPurchased: Int = 0,
    val lastAddedAt: Long = System.currentTimeMillis(),
    val lastPurchasedAt: Long? = null
) {
    val categoryEnum: GroceryCategory
        get() = GroceryCategory.fromId(categoryId)

    // Transparent, deterministic ranking score based on purchase frequency and recent use
    val rankingScore: Int
        get() = (timesPurchased * 3) + timesAdded
}
