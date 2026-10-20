package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.FavoriteItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorite_items ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT * FROM favorite_items WHERE LOWER(canonicalName) = LOWER(:name) LIMIT 1")
    suspend fun findFavorite(name: String): FavoriteItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItem): Long

    @Query("DELETE FROM favorite_items WHERE LOWER(canonicalName) = LOWER(:name)")
    suspend fun deleteFavoriteByName(name: String)

    @Query("DELETE FROM favorite_items WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)
}
