package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {

    @Query("SELECT * FROM grocery_items WHERE listId = :listId ORDER BY isPurchased ASC, createdAt DESC")
    fun getItemsForList(listId: Long): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE listId = :listId ORDER BY isPurchased ASC, createdAt DESC")
    suspend fun getItemsForListDirect(listId: Long): List<GroceryItem>

    @Query("SELECT * FROM grocery_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): GroceryItem?

    @Query("SELECT * FROM grocery_items WHERE listId = :listId AND normalizedName = :normalizedName LIMIT 1")
    suspend fun findItemInList(listId: Long, normalizedName: String): GroceryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GroceryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<GroceryItem>)

    @Update
    suspend fun updateItem(item: GroceryItem)

    @Query("UPDATE grocery_items SET isPurchased = :isPurchased, purchasedAt = :purchasedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePurchasedStatus(id: Long, isPurchased: Boolean, purchasedAt: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE grocery_items SET quantity = :quantity, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Double, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE grocery_items SET categoryId = :categoryId, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCategory(id: Long, categoryId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT * FROM grocery_items WHERE listId = :listId AND isPurchased = 1")
    suspend fun getPurchasedItemsInList(listId: Long): List<GroceryItem>

    @Query("DELETE FROM grocery_items WHERE listId = :listId AND isPurchased = 1")
    suspend fun deletePurchasedItemsInList(listId: Long): Int
}
