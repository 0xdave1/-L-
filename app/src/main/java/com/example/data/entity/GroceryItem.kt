package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.GroceryCategory

@Entity(
    tableName = "grocery_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("listId"),
        Index("normalizedName")
    ]
)
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listId: Long,
    val name: String,
    val normalizedName: String = name.trim().lowercase(),
    val quantity: Double = 1.0,
    val unit: String = "Piece",
    val categoryId: String = GroceryCategory.OTHER.id,
    val estimatedUnitPriceMinorUnits: Long? = null,
    val actualUnitPriceMinorUnits: Long? = null,
    val actualLineTotalMinorUnits: Long? = null,
    val useActualLineTotal: Boolean = false,
    val notes: String = "",
    val isPurchased: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val purchasedAt: Long? = null
) {
    val categoryEnum: GroceryCategory
        get() = GroceryCategory.fromId(categoryId)

    val estimatedLineTotalMinorUnits: Long
        get() {
            val unitPrice = estimatedUnitPriceMinorUnits ?: 0L
            return (quantity * unitPrice).toLong()
        }

    val actualConfirmedLineTotalMinorUnits: Long?
        get() {
            if (!isPurchased) return null
            if (useActualLineTotal && actualLineTotalMinorUnits != null) {
                return actualLineTotalMinorUnits
            }
            if (actualUnitPriceMinorUnits != null) {
                return (quantity * actualUnitPriceMinorUnits).toLong()
            }
            return null
        }
}
