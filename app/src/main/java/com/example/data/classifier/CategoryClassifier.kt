package com.example.data.classifier

import com.example.data.model.GroceryCategory
import com.example.data.model.NigerianUnits

object CategoryClassifier {

    /**
     * Deterministic classification pipeline.
     * Takes an item name, optional user-saved override map, and returns the assigned category.
     */
    fun classify(
        rawName: String,
        userRules: Map<String, String> = emptyMap()
    ): GroceryCategory {
        val trimmed = rawName.trim()
        if (trimmed.isEmpty()) return GroceryCategory.OTHER

        val normalized = trimmed.lowercase()

        // 1. Check user-saved custom category rule
        userRules[normalized]?.let { catId ->
            return GroceryCategory.fromId(catId)
        }

        // 2. Exact match on catalog canonical names or aliases
        for (entry in NigerianGroceryCatalog.entries) {
            if (entry.canonicalName.equals(normalized, ignoreCase = true)) {
                return entry.category
            }
            if (entry.aliases.any { it.equals(normalized, ignoreCase = true) }) {
                return entry.category
            }
        }

        // 3. Normalized tokens without special punctuation
        val cleanWords = normalized.replace(Regex("[^a-z0-9\\s]"), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        // 4. Substring / Alias matching within phrase
        for (entry in NigerianGroceryCatalog.entries) {
            val canon = entry.canonicalName.lowercase()
            if (normalized.contains(canon)) {
                return entry.category
            }
            for (alias in entry.aliases) {
                if (normalized.contains(alias.lowercase())) {
                    return entry.category
                }
            }
        }

        // 5. Word-by-word token rules with singularization
        for (word in cleanWords) {
            val matched = matchWordToCategory(word)
            if (matched != null) return matched

            if (word.length > 3 && word.endsWith("es")) {
                val singular = word.removeSuffix("es")
                val singularMatch = matchWordToCategory(singular)
                if (singularMatch != null) return singularMatch
            } else if (word.length > 2 && word.endsWith("s")) {
                val singular = word.removeSuffix("s")
                val singularMatch = matchWordToCategory(singular)
                if (singularMatch != null) return singularMatch
            }
        }

        // 6. Fallback to OTHER when confidence is insufficient
        return GroceryCategory.OTHER
    }

    fun suggestUnit(rawName: String): String {
        val normalized = rawName.trim().lowercase()
        for (entry in NigerianGroceryCatalog.entries) {
            if (entry.canonicalName.equals(normalized, ignoreCase = true) ||
                entry.aliases.any { it.equals(normalized, ignoreCase = true) } ||
                normalized.contains(entry.canonicalName.lowercase())) {
                return entry.defaultUnit
            }
        }
        return NigerianUnits.PIECE
    }

    private fun matchWordToCategory(word: String): GroceryCategory? {
        return when (word) {
            // Fruits & Vegetables
            "tomato", "pepper", "rodo", "tatashe", "shombo", "onion", "okra", "okro",
            "spinach", "ugu", "ugwu", "bitterleaf", "waterleaf", "ewedu", "oha", "uziza",
            "carrot", "cabbage", "plantain", "banana", "orange", "apple", "fruit", "vegetable",
            "garlic", "ginger", "cucumber", "watermelon", "pineapple", "mango", "avocado", "herb" ->
                GroceryCategory.FRUITS_AND_VEGETABLES

            // Grains & Staples
            "rice", "bean", "beans", "garri", "gari", "yam", "elubo", "cassava", "flour",
            "semovita", "semolina", "semo", "poundo", "oat", "oats", "maize", "corn",
            "millet", "wheat", "fufu", "akpu", "abacha", "tapioca", "noodle", "spaghetti", "pasta" ->
                GroceryCategory.GRAINS_AND_STAPLES

            // Meat & Poultry
            "chicken", "beef", "meat", "goat", "turkey", "liver", "kidney", "shaki", "tripe",
            "kpomo", "ponmo", "kanda", "suya", "pork", "mutton", "sausage", "bacon", "steak" ->
                GroceryCategory.MEAT_AND_POULTRY

            // Fish & Seafood
            "fish", "catfish", "tilapia", "titus", "mackerel", "stockfish", "okporoko",
            "crayfish", "prawn", "shrimp", "snail", "periwinkle", "crab", "seafood" ->
                GroceryCategory.FISH_AND_SEAFOOD

            // Dairy & Eggs
            "egg", "milk", "yoghurt", "yogurt", "butter", "cheese", "margarine", "cream", "wara" ->
                GroceryCategory.DAIRY_AND_EGGS

            // Bakery
            "bread", "agege", "bun", "buns", "puff", "pie", "croissant", "toast", "loaf",
            "pastry", "cake", "cupcake", "donut", "doughnut" ->
                GroceryCategory.BAKERY

            // Cooking Ingredients
            "ororo", "maggi", "knorr", "royco", "onga",
            "curry", "thyme", "egusi", "ogbono", "apon", "iru", "nutmeg",
            "spices", "vinegar" ->
                GroceryCategory.COOKING_INGREDIENTS

            // Beverages
            "milo", "bournvita", "tea", "coffee", "zobo", "malt", "drink", "juice", "coke",
            "fanta", "sprite", "soda", "water", "beverage", "chivita", "pepsi" ->
                GroceryCategory.BEVERAGES

            // Snacks & Confectionery
            "chinchin", "chips", "groundnut", "peanut", "cashew", "biscuit", "cookie",
            "chocolate", "candy", "sweet", "popcorn", "crisps", "snack" ->
                GroceryCategory.SNACKS_AND_CONFECTIONERY

            // Frozen
            "frozen", "ice", "gelato", "popsicle" ->
                GroceryCategory.FROZEN_FOODS

            // Personal Care
            "soap", "dudu", "toothpaste", "toothbrush", "lotion", "cream", "vaseline",
            "shampoo", "pad", "pads", "deodorant", "perfume", "cosmetics", "toiletries" ->
                GroceryCategory.PERSONAL_CARE

            // Cleaning Supplies
            "detergent", "omo", "ariel", "sunlight", "dishwashing", "bleach", "jik", "hypo",
            "disinfectant", "dettol", "sponge", "harpic", "cleaner", "mop", "broom" ->
                GroceryCategory.CLEANING_SUPPLIES

            // Household Essentials
            "tissue", "serviette", "napkin", "trash", "match", "matches", "battery", "batteries",
            "bulb", "bulbs", "foil", "insecticide", "raid", "baygon" ->
                GroceryCategory.HOUSEHOLD_ESSENTIALS

            else -> null
        }
    }
}
