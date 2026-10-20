package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class GroceryCategory(
    val id: String,
    val displayName: String,
    val emoji: String,
    val sortOrder: Int,
    val accentColor: Color,
    val containerColor: Color
) {
    FRUITS_AND_VEGETABLES(
        id = "fruits_vegetables",
        displayName = "Fruits and Vegetables",
        emoji = "🍅",
        sortOrder = 1,
        accentColor = CatFruitsVegetables,
        containerColor = CatFruitsVegetablesBg
    ),
    GRAINS_AND_STAPLES(
        id = "grains_staples",
        displayName = "Grains and Staples",
        emoji = "🌾",
        sortOrder = 2,
        accentColor = CatGrainsStaples,
        containerColor = CatGrainsStaplesBg
    ),
    MEAT_AND_POULTRY(
        id = "meat_poultry",
        displayName = "Meat and Poultry",
        emoji = "🍗",
        sortOrder = 3,
        accentColor = CatMeatPoultry,
        containerColor = CatMeatPoultryBg
    ),
    FISH_AND_SEAFOOD(
        id = "fish_seafood",
        displayName = "Fish and Seafood",
        emoji = "🐟",
        sortOrder = 4,
        accentColor = CatFishSeafood,
        containerColor = CatFishSeafoodBg
    ),
    DAIRY_AND_EGGS(
        id = "dairy_eggs",
        displayName = "Dairy and Eggs",
        emoji = "🥛",
        sortOrder = 5,
        accentColor = CatDairyEggs,
        containerColor = CatDairyEggsBg
    ),
    BAKERY(
        id = "bakery",
        displayName = "Bakery",
        emoji = "🍞",
        sortOrder = 6,
        accentColor = CatBakery,
        containerColor = CatBakeryBg
    ),
    COOKING_INGREDIENTS(
        id = "cooking_ingredients",
        displayName = "Cooking Ingredients",
        emoji = "🫒",
        sortOrder = 7,
        accentColor = CatCookingIngredients,
        containerColor = CatCookingIngredientsBg
    ),
    BEVERAGES(
        id = "beverages",
        displayName = "Beverages",
        emoji = "🧃",
        sortOrder = 8,
        accentColor = CatBeverages,
        containerColor = CatBeveragesBg
    ),
    SNACKS_AND_CONFECTIONERY(
        id = "snacks_confectionery",
        displayName = "Snacks & Confectionery",
        emoji = "🍿",
        sortOrder = 9,
        accentColor = CatSnacks,
        containerColor = CatSnacksBg
    ),
    FROZEN_FOODS(
        id = "frozen_foods",
        displayName = "Frozen Foods",
        emoji = "🧊",
        sortOrder = 10,
        accentColor = CatFrozenFoods,
        containerColor = CatFrozenFoodsBg
    ),
    PERSONAL_CARE(
        id = "personal_care",
        displayName = "Personal Care",
        emoji = "🧼",
        sortOrder = 11,
        accentColor = CatPersonalCare,
        containerColor = CatPersonalCareBg
    ),
    CLEANING_SUPPLIES(
        id = "cleaning_supplies",
        displayName = "Cleaning Supplies",
        emoji = "🧹",
        sortOrder = 12,
        accentColor = CatCleaningSupplies,
        containerColor = CatCleaningSuppliesBg
    ),
    HOUSEHOLD_ESSENTIALS(
        id = "household_essentials",
        displayName = "Household Essentials",
        emoji = "💡",
        sortOrder = 13,
        accentColor = CatHouseholdEssentials,
        containerColor = CatHouseholdEssentialsBg
    ),
    OTHER(
        id = "other",
        displayName = "Other",
        emoji = "🛒",
        sortOrder = 14,
        accentColor = CatOther,
        containerColor = CatOtherBg
    );

    companion object {
        fun fromId(id: String?): GroceryCategory {
            if (id == null) return OTHER
            return values().find { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
