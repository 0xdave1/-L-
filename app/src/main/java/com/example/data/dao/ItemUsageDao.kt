package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.ItemUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemUsageDao {

    @Query("SELECT * FROM item_usage ORDER BY ((timesPurchased * 3) + timesAdded) DESC, lastPurchasedAt DESC LIMIT 20")
    fun getFrequentlyBought(): Flow<List<ItemUsage>>

    @Query("SELECT * FROM item_usage WHERE normalizedItemName = :normalized LIMIT 1")
    suspend fun getUsage(normalized: String): ItemUsage?

    @Query("""
        INSERT INTO item_usage (normalizedItemName, displayName, categoryId, timesAdded, timesPurchased, lastAddedAt, lastPurchasedAt)
        VALUES (:normalized, :displayName, :categoryId, 1, 0, :timestamp, NULL)
        ON CONFLICT(normalizedItemName) DO UPDATE SET
            timesAdded = timesAdded + 1,
            lastAddedAt = :timestamp
    """)
    suspend fun recordItemAdded(normalized: String, displayName: String, categoryId: String, timestamp: Long)

    @Query("""
        UPDATE item_usage SET
            timesPurchased = timesPurchased + 1,
            lastPurchasedAt = :timestamp
        WHERE normalizedItemName = :normalized
    """)
    suspend fun recordItemPurchased(normalized: String, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsages(items: List<ItemUsage>)
}
