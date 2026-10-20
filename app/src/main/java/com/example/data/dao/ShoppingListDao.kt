package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists ORDER BY updatedAt DESC")
    fun getAllLists(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_lists WHERE status = 'ACTIVE' ORDER BY updatedAt DESC")
    fun getActiveLists(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_lists WHERE status = 'COMPLETED' ORDER BY updatedAt DESC")
    fun getCompletedLists(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id LIMIT 1")
    fun getListById(id: Long): Flow<ShoppingList?>

    @Query("SELECT * FROM shopping_lists WHERE id = :id LIMIT 1")
    suspend fun getListByIdDirect(id: Long): ShoppingList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingList): Long

    @Update
    suspend fun updateList(list: ShoppingList)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteListById(id: Long)

    @Query("UPDATE shopping_lists SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE shopping_lists SET name = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateName(id: Long, name: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE shopping_lists SET budgetMinorUnits = :budget, currencyCode = :currency, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateBudget(id: Long, budget: Long?, currency: String, updatedAt: Long = System.currentTimeMillis())
}
