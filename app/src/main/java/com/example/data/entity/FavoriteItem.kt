package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.GroceryCategory

@Entity(
    tableName = "favorite_items",
    indices = [Index(value = ["canonicalName"], unique = true)]
)
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val canonicalName: String,
    val categoryId: String = GroceryCategory.OTHER.id,
    val defaultUnit: String = "Piece",
    val defaultQuantity: Double = 1.0,
    val defaultEstimatedPriceMinorUnits: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val categoryEnum: GroceryCategory
        get() = GroceryCategory.fromId(categoryId)
}
