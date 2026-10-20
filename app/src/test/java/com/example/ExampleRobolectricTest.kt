package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.classifier.CategoryClassifier
import com.example.data.database.GroceryDatabase
import com.example.data.entity.ShoppingList
import com.example.data.model.Currencies
import com.example.data.model.GroceryCategory
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: GroceryDatabase
    private lateinit var repository: GroceryRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GroceryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GroceryRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ÈLÒ", appName)
    }

    @Test
    fun testNigerianCategoryClassificationRules() {
        // From user spec Section 6:
        // Input: "Rice" -> Grains and Staples
        assertEquals(GroceryCategory.GRAINS_AND_STAPLES, CategoryClassifier.classify("Rice"))
        // Input: "Ugu" -> Fruits and Vegetables
        assertEquals(GroceryCategory.FRUITS_AND_VEGETABLES, CategoryClassifier.classify("Ugu"))
        // Input: "Detergent" -> Cleaning Supplies
        assertEquals(GroceryCategory.CLEANING_SUPPLIES, CategoryClassifier.classify("Detergent"))
        // Input: "Agege bread" -> Bakery
        assertEquals(GroceryCategory.BAKERY, CategoryClassifier.classify("Agege bread"))
        // Unmatched -> Other
        assertEquals(GroceryCategory.OTHER, CategoryClassifier.classify("My special seasoning"))

        // More authentic Nigerian staples
        assertEquals(GroceryCategory.GRAINS_AND_STAPLES, CategoryClassifier.classify("Garri"))
        assertEquals(GroceryCategory.GRAINS_AND_STAPLES, CategoryClassifier.classify("Yam"))
        assertEquals(GroceryCategory.FRUITS_AND_VEGETABLES, CategoryClassifier.classify("Tomatoes"))
        assertEquals(GroceryCategory.FRUITS_AND_VEGETABLES, CategoryClassifier.classify("Plantain"))
        assertEquals(GroceryCategory.FRUITS_AND_VEGETABLES, CategoryClassifier.classify("Ata rodo"))
        assertEquals(GroceryCategory.COOKING_INGREDIENTS, CategoryClassifier.classify("Palm oil"))
        assertEquals(GroceryCategory.COOKING_INGREDIENTS, CategoryClassifier.classify("Maggi cubes"))
        assertEquals(GroceryCategory.COOKING_INGREDIENTS, CategoryClassifier.classify("Egusi"))
        assertEquals(GroceryCategory.FISH_AND_SEAFOOD, CategoryClassifier.classify("Stockfish"))
        assertEquals(GroceryCategory.FISH_AND_SEAFOOD, CategoryClassifier.classify("Crayfish"))
        assertEquals(GroceryCategory.MEAT_AND_POULTRY, CategoryClassifier.classify("Chicken"))
        assertEquals(GroceryCategory.MEAT_AND_POULTRY, CategoryClassifier.classify("Beef"))
        assertEquals(GroceryCategory.DAIRY_AND_EGGS, CategoryClassifier.classify("Peak Milk"))
        assertEquals(GroceryCategory.DAIRY_AND_EGGS, CategoryClassifier.classify("Eggs"))
        assertEquals(GroceryCategory.HOUSEHOLD_ESSENTIALS, CategoryClassifier.classify("Tissue paper"))
        assertEquals(GroceryCategory.PERSONAL_CARE, CategoryClassifier.classify("Toothpaste"))
        assertEquals(GroceryCategory.BEVERAGES, CategoryClassifier.classify("Zobo"))
        assertEquals(GroceryCategory.SNACKS_AND_CONFECTIONERY, CategoryClassifier.classify("Chin chin"))
    }

    @Test
    fun testUserCategoryOverridePersistence() {
        // User rule overrides default
        val userRules = mapOf("my special seasoning" to GroceryCategory.COOKING_INGREDIENTS.id)
        val result = CategoryClassifier.classify("My special seasoning", userRules)
        assertEquals(GroceryCategory.COOKING_INGREDIENTS, result)
    }

    @Test
    fun testCurrencyFormattingAndMinorUnits() {
        val currency = Currencies.NGN
        // ₦1,500
        assertEquals("₦1,500", currency.formatAmount(150000L))
        // ₦12,500
        assertEquals("₦12,500", currency.formatAmount(1250000L))
        // ₦150,000
        assertEquals("₦150,000", currency.formatAmount(15000000L))

        // Safe parsing
        assertEquals(2500000L, currency.parseToMinorUnits("25000"))
        assertEquals(5000000L, currency.parseToMinorUnits("₦50,000"))
    }

    @Test
    fun testShoppingListLifecycleAndCalculations() = runBlocking {
        // 1. Create list with ₦20,000 budget (2,000,000 minor units)
        val listId = repository.createList(
            name = "Weekly Market",
            budgetMinorUnits = 2000000L,
            currencyCode = "NGN"
        )
        assertTrue(listId > 0)

        val lists = repository.activeLists.first()
        assertEquals(1, lists.size)
        assertEquals("Weekly Market", lists.first().name)

        // 2. Add items
        val item1Id = repository.addItem(
            listId = listId,
            name = "Rice",
            quantity = 2.0,
            unit = "Mudu",
            estimatedUnitPriceMinorUnits = 300000L // ₦3,000
        )
        val item2Id = repository.addItem(
            listId = listId,
            name = "Tomatoes",
            quantity = 1.0,
            unit = "Basket",
            estimatedUnitPriceMinorUnits = 500000L // ₦5,000
        )

        var items = repository.getItems(listId).first()
        assertEquals(2, items.size)

        val riceItem = items.first { it.id == item1Id }
        assertEquals(GroceryCategory.GRAINS_AND_STAPLES.id, riceItem.categoryId)
        assertEquals(600000L, riceItem.estimatedLineTotalMinorUnits) // 2 * 3,000 = 6,000
        assertFalse(riceItem.isPurchased)
        assertNull(riceItem.actualConfirmedLineTotalMinorUnits) // Unchecked item doesn't count as spent

        // 3. Tick off Rice as purchased
        repository.toggleItemPurchased(riceItem)
        items = repository.getItems(listId).first()
        val purchasedRice = items.first { it.id == item1Id }
        assertTrue(purchasedRice.isPurchased)

        // 4. Duplicate list for a new shopping trip
        val duplicatedListId = repository.duplicateList(listId, "Next Week Market")
        val dupItems = repository.getItems(duplicatedListId).first()
        assertEquals(2, dupItems.size)
        // All duplicated items should have isPurchased reset to false
        assertTrue(dupItems.all { !it.isPurchased })

        // 5. Clear purchased items from original list
        val cleared = repository.clearPurchasedItems(listId)
        assertEquals(1, cleared.size)
        assertEquals("Rice", cleared.first().name)

        val remaining = repository.getItems(listId).first()
        assertEquals(1, remaining.size)
        assertEquals("Tomatoes", remaining.first().name)
        assertFalse(remaining.first().isPurchased)
    }
}
