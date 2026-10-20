package com.example.data.repository

import com.example.data.classifier.CategoryClassifier
import com.example.data.database.GroceryDatabase
import com.example.data.entity.FavoriteItem
import com.example.data.entity.GroceryItem
import com.example.data.entity.ItemUsage
import com.example.data.entity.ShoppingList
import com.example.data.entity.UserCategoryRule
import com.example.data.model.GroceryCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GroceryRepository(private val db: GroceryDatabase) {

    private val listDao = db.shoppingListDao()
    private val itemDao = db.groceryItemDao()
    private val favDao = db.favoritesDao()
    private val usageDao = db.itemUsageDao()
    private val ruleDao = db.userCategoryRuleDao()

    val allLists: Flow<List<ShoppingList>> = listDao.getAllLists()
    val activeLists: Flow<List<ShoppingList>> = listDao.getActiveLists()
    val completedLists: Flow<List<ShoppingList>> = listDao.getCompletedLists()
    val favorites: Flow<List<FavoriteItem>> = favDao.getAllFavorites()
    val frequentlyBought: Flow<List<ItemUsage>> = usageDao.getFrequentlyBought()
    val userCategoryRules: Flow<List<UserCategoryRule>> = ruleDao.getAllRules()

    fun getList(id: Long): Flow<ShoppingList?> = listDao.getListById(id)
    fun getItems(listId: Long): Flow<List<GroceryItem>> = itemDao.getItemsForList(listId)

    suspend fun createList(
        name: String,
        budgetMinorUnits: Long? = null,
        currencyCode: String = "NGN",
        shoppingDate: Long? = null
    ): Long {
        val cleanName = name.trim().ifBlank { "Shopping List" }
        val list = ShoppingList(
            name = cleanName,
            budgetMinorUnits = budgetMinorUnits,
            currencyCode = currencyCode,
            shoppingDate = shoppingDate,
            status = ShoppingList.STATUS_ACTIVE
        )
        return listDao.insertList(list)
    }

    suspend fun renameList(listId: Long, newName: String) {
        val cleanName = newName.trim().ifBlank { "Shopping List" }
        listDao.updateName(listId, cleanName)
    }

    suspend fun updateBudget(listId: Long, budgetMinorUnits: Long?, currencyCode: String) {
        listDao.updateBudget(listId, budgetMinorUnits, currencyCode)
    }

    suspend fun markListCompleted(listId: Long) {
        listDao.updateStatus(listId, ShoppingList.STATUS_COMPLETED)
    }

    suspend fun reopenList(listId: Long) {
        listDao.updateStatus(listId, ShoppingList.STATUS_ACTIVE)
    }

    suspend fun deleteList(listId: Long) {
        listDao.deleteListById(listId)
    }

    suspend fun duplicateList(listId: Long, newName: String? = null): Long {
        val originalList = listDao.getListByIdDirect(listId) ?: return -1
        val originalItems = itemDao.getItemsForListDirect(listId)

        val targetName = newName?.trim()?.ifBlank { null } ?: "${originalList.name} (Copy)"
        val newList = ShoppingList(
            name = targetName,
            budgetMinorUnits = originalList.budgetMinorUnits,
            currencyCode = originalList.currencyCode,
            status = ShoppingList.STATUS_ACTIVE
        )
        val newListId = listDao.insertList(newList)

        // Copy items: reset purchased status, clear actual spending, preserve estimated prices
        val copiedItems = originalItems.map { oldItem ->
            GroceryItem(
                listId = newListId,
                name = oldItem.name,
                normalizedName = oldItem.normalizedName,
                quantity = oldItem.quantity,
                unit = oldItem.unit,
                categoryId = oldItem.categoryId,
                estimatedUnitPriceMinorUnits = oldItem.estimatedUnitPriceMinorUnits,
                actualUnitPriceMinorUnits = null,
                actualLineTotalMinorUnits = null,
                useActualLineTotal = false,
                notes = oldItem.notes,
                isPurchased = false
            )
        }
        itemDao.insertItems(copiedItems)
        return newListId
    }

    suspend fun checkExistingItem(listId: Long, name: String): GroceryItem? {
        val normalized = name.trim().lowercase()
        return itemDao.findItemInList(listId, normalized)
    }

    suspend fun addItem(
        listId: Long,
        name: String,
        quantity: Double = 1.0,
        unit: String? = null,
        estimatedUnitPriceMinorUnits: Long? = null,
        categoryOverride: GroceryCategory? = null,
        notes: String = ""
    ): Long {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return -1

        val normalized = cleanName.lowercase()

        // Get user category rules for deterministic resolution
        val rulesMap = ruleDao.getAllRulesDirect().associate { it.normalizedItemName to it.categoryId }

        val category = categoryOverride ?: CategoryClassifier.classify(cleanName, rulesMap)
        val selectedUnit = unit ?: CategoryClassifier.suggestUnit(cleanName)

        val item = GroceryItem(
            listId = listId,
            name = cleanName,
            normalizedName = normalized,
            quantity = if (quantity <= 0.0) 1.0 else quantity,
            unit = selectedUnit,
            categoryId = category.id,
            estimatedUnitPriceMinorUnits = estimatedUnitPriceMinorUnits,
            notes = notes,
            isPurchased = false
        )

        val id = itemDao.insertItem(item)

        // Record usage for transparent frequency tracking
        usageDao.recordItemAdded(
            normalized = normalized,
            displayName = cleanName,
            categoryId = category.id,
            timestamp = System.currentTimeMillis()
        )

        // Update list's updatedAt
        listDao.updateStatus(listId, ShoppingList.STATUS_ACTIVE)

        return id
    }

    suspend fun updateItem(item: GroceryItem) {
        itemDao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleItemPurchased(item: GroceryItem) {
        val newStatus = !item.isPurchased
        val timestamp = if (newStatus) System.currentTimeMillis() else null
        itemDao.updatePurchasedStatus(item.id, newStatus, timestamp)

        if (newStatus) {
            usageDao.recordItemPurchased(item.normalizedName, System.currentTimeMillis())
        }
    }

    suspend fun updateQuantity(itemId: Long, quantity: Double) {
        if (quantity <= 0.0) {
            itemDao.deleteItemById(itemId)
        } else {
            itemDao.updateQuantity(itemId, quantity)
        }
    }

    suspend fun updateCategory(
        itemId: Long,
        itemName: String,
        newCategory: GroceryCategory,
        rememberForFuture: Boolean = true
    ) {
        itemDao.updateCategory(itemId, newCategory.id)

        if (rememberForFuture) {
            val normalized = itemName.trim().lowercase()
            ruleDao.saveRule(UserCategoryRule(normalizedItemName = normalized, categoryId = newCategory.id))
        }
    }

    suspend fun deleteItem(itemId: Long) {
        itemDao.deleteItemById(itemId)
    }

    suspend fun clearPurchasedItems(listId: Long): List<GroceryItem> {
        val purchased = itemDao.getPurchasedItemsInList(listId)
        itemDao.deletePurchasedItemsInList(listId)
        return purchased
    }

    suspend fun restoreItems(items: List<GroceryItem>) {
        itemDao.insertItems(items)
    }

    // Favorites
    suspend fun toggleFavorite(name: String, categoryId: String, unit: String) {
        val existing = favDao.findFavorite(name)
        if (existing != null) {
            favDao.deleteFavoriteById(existing.id)
        } else {
            favDao.insertFavorite(
                FavoriteItem(
                    canonicalName = name.trim(),
                    categoryId = categoryId,
                    defaultUnit = unit
                )
            )
        }
    }

    suspend fun deleteFavorite(id: Long) {
        favDao.deleteFavoriteById(id)
    }
}
