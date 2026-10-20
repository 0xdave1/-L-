package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.GroceryCategory

@Entity(tableName = "user_category_rules")
data class UserCategoryRule(
    @PrimaryKey
    val normalizedItemName: String,
    val categoryId: String,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val categoryEnum: GroceryCategory
        get() = GroceryCategory.fromId(categoryId)
}
