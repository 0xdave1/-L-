package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FavoritesDao
import com.example.data.dao.GroceryItemDao
import com.example.data.dao.ItemUsageDao
import com.example.data.dao.ShoppingListDao
import com.example.data.dao.UserCategoryRuleDao
import com.example.data.entity.FavoriteItem
import com.example.data.entity.GroceryItem
import com.example.data.entity.ItemUsage
import com.example.data.entity.ShoppingList
import com.example.data.entity.UserCategoryRule
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [
        ShoppingList::class,
        GroceryItem::class,
        FavoriteItem::class,
        ItemUsage::class,
        UserCategoryRule::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GroceryDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun itemUsageDao(): ItemUsageDao
    abstract fun userCategoryRuleDao(): UserCategoryRuleDao

    companion object {
        @Volatile
        private var INSTANCE: GroceryDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GroceryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GroceryDatabase::class.java,
                    "smart_grocery_list_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
