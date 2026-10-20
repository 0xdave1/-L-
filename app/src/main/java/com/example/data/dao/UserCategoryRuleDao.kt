package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.UserCategoryRule
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCategoryRuleDao {

    @Query("SELECT * FROM user_category_rules")
    fun getAllRules(): Flow<List<UserCategoryRule>>

    @Query("SELECT * FROM user_category_rules")
    suspend fun getAllRulesDirect(): List<UserCategoryRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRule(rule: UserCategoryRule)
}
