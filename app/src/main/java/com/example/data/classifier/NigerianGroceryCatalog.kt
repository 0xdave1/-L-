package com.example.data.classifier

import com.example.data.model.GroceryCategory
import com.example.data.model.NigerianUnits

data class CatalogEntry(
    val canonicalName: String,
    val category: GroceryCategory,
    val defaultUnit: String,
    val aliases: List<String> = emptyList(),
    val searchKeywords: List<String> = emptyList()
)

object NigerianGroceryCatalog {

    val entries: List<CatalogEntry> = listOf(
        // 1. Fruits and Vegetables
        CatalogEntry("Tomatoes", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("tomato", "fresh tomatoes"), listOf("stew", "sauce")),
        CatalogEntry("Fresh Pepper", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("ata rodo", "rodo", "scotch bonnet", "pepper", "habanero")),
        CatalogEntry("Tatashe", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("bell pepper", "red bell pepper")),
        CatalogEntry("Shombo", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("chili pepper", "cayenne")),
        CatalogEntry("Onions", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BAG, listOf("onion", "red onions", "white onions")),
        CatalogEntry("Okra", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("okro", "lady finger")),
        CatalogEntry("Spinach", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("green leaf", "efo tete")),
        CatalogEntry("Ugu", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("fluted pumpkin", "pumpkin leaves", "ugwu")),
        CatalogEntry("Bitter Leaf", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("ewuro", "olubu")),
        CatalogEntry("Waterleaf", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("gbure")),
        CatalogEntry("Ewedu", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("jute leaves", "ayoyo")),
        CatalogEntry("Oha Leaves", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("ora leaves")),
        CatalogEntry("Uziza", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("uziza leaves", "hot leaf")),
        CatalogEntry("Scent Leaf", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("effirin", "nchanwu", "basil")),
        CatalogEntry("Carrots", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.KG, listOf("carrot")),
        CatalogEntry("Cabbage", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.PIECE, listOf("cabbage head")),
        CatalogEntry("Plantain", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("ripe plantain", "unripe plantain", "dodo", "boli")),
        CatalogEntry("Bananas", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BUNCH, listOf("banana")),
        CatalogEntry("Oranges", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("orange", "sweet orange")),
        CatalogEntry("Watermelon", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.PIECE, listOf("melon fruit")),
        CatalogEntry("Pineapple", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.PIECE),
        CatalogEntry("Cucumber", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.PIECE),
        CatalogEntry("Garden Egg", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.BASKET, listOf("eggplant", "anara", "ganyen")),
        CatalogEntry("Ginger", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.KG),
        CatalogEntry("Garlic", GroceryCategory.FRUITS_AND_VEGETABLES, NigerianUnits.PACK),

        // 2. Grains and Staples
        CatalogEntry("Rice", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.BAG, listOf("foreign rice", "local rice", "parboiled rice", "caprice", "mama gold")),
        CatalogEntry("Ofada Rice", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.BAG, listOf("local brown rice", "ofada")),
        CatalogEntry("Beans", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.MUDU, listOf("oloyin", "honey beans", "brown beans", "white beans", "black eyed peas")),
        CatalogEntry("Garri", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.MUDU, listOf("ijebu garri", "yellow garri", "white garri", "gari", "eba")),
        CatalogEntry("Yam", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PIECE, listOf("tubers of yam", "tuber of yam", "white yam", "abuja yam")),
        CatalogEntry("Cassava Flour", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.BAG, listOf("elubo", "lafun")),
        CatalogEntry("Semolina", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PACK, listOf("semovita", "semo")),
        CatalogEntry("Poundo Yam", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PACK, listOf("poundo", "pounded yam flour")),
        CatalogEntry("Oats", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PACK, listOf("quaker oats", "rolled oats")),
        CatalogEntry("Maize", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.MUDU, listOf("corn", "yellow corn", "white corn")),
        CatalogEntry("Millet", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.MUDU, listOf("jero")),
        CatalogEntry("Wheat Flour", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.BAG, listOf("flour", "all purpose flour", "baking flour")),
        CatalogEntry("Fufu", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PACK, listOf("akpu", "wet fufu")),
        CatalogEntry("Abacha", GroceryCategory.GRAINS_AND_STAPLES, NigerianUnits.PACK, listOf("african salad", "tapioca")),

        // 3. Meat and Poultry
        CatalogEntry("Chicken", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("broiler", "old layer", "local chicken", "chicken laps", "chicken wings")),
        CatalogEntry("Beef", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("cow meat", "beef cuts", "minced beef", "ground beef")),
        CatalogEntry("Goat Meat", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("asun", "he-goat", "she-goat")),
        CatalogEntry("Turkey", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("turkey wings", "smoked turkey")),
        CatalogEntry("Liver", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("cow liver")),
        CatalogEntry("Shaki", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.KG, listOf("tripe", "towel", "cow tripe")),
        CatalogEntry("Kpomo", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.PIECE, listOf("ponmo", "cow skin", "kanda")),
        CatalogEntry("Suya", GroceryCategory.MEAT_AND_POULTRY, NigerianUnits.PACK, listOf("kilishi", "beef skewers")),

        // 4. Fish and Seafood
        CatalogEntry("Fresh Fish", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.KG, listOf("catfish", "tilapia", "croaker")),
        CatalogEntry("Titus Fish", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.CARTON, listOf("mackerel", "titus")),
        CatalogEntry("Smoked Fish", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.PIECE, listOf("dried catfish", "mangala", "panla")),
        CatalogEntry("Stockfish", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.PIECE, listOf("okporoko", "stockfish head", "cod")),
        CatalogEntry("Crayfish", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.MUDU, listOf("ground crayfish", "prawns", "creyfish")),
        CatalogEntry("Prawns", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.KG, listOf("shrimps", "fresh prawns")),
        CatalogEntry("Snails", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.PIECE, listOf("snail", "giant snail", "congo meat")),
        CatalogEntry("Periwinkle", GroceryCategory.FISH_AND_SEAFOOD, NigerianUnits.PACK, listOf("sam sam")),

        // 5. Dairy and Eggs
        CatalogEntry("Eggs", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.CRATE, listOf("crate of eggs", "egg")),
        CatalogEntry("Milk", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.CARTON, listOf("peak milk", "dano milk", "cowbell", "evaporated milk")),
        CatalogEntry("Powdered Milk", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.SACHET, listOf("powder milk", "milk sachet")),
        CatalogEntry("Yoghurt", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.BOTTLE, listOf("yogurt", "fanyogo", "habeeb")),
        CatalogEntry("Butter", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.PACK, listOf("blue band", "margarine")),
        CatalogEntry("Cheese", GroceryCategory.DAIRY_AND_EGGS, NigerianUnits.PACK, listOf("wara", "cheddar")),

        // 6. Bakery
        CatalogEntry("Bread", GroceryCategory.BAKERY, NigerianUnits.PIECE, listOf("agege bread", "sliced bread", "sweet bread", "loaf of bread")),
        CatalogEntry("Buns", GroceryCategory.BAKERY, NigerianUnits.PACK, listOf("puff puff", "fried buns")),
        CatalogEntry("Meat Pie", GroceryCategory.BAKERY, NigerianUnits.PIECE, listOf("chicken pie", "fish pie")),
        CatalogEntry("Sausage Roll", GroceryCategory.BAKERY, NigerianUnits.PIECE, listOf("gala", "beef roll")),
        CatalogEntry("Croissants", GroceryCategory.BAKERY, NigerianUnits.PACK),
        CatalogEntry("Cake", GroceryCategory.BAKERY, NigerianUnits.PIECE, listOf("cupcakes", "birthday cake")),

        // 7. Cooking Ingredients
        CatalogEntry("Palm Oil", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.BOTTLE, listOf("red oil", "epo pupa", "palm-oil")),
        CatalogEntry("Groundnut Oil", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.BOTTLE, listOf("vegetable oil", "cooking oil", "ororo", "kings oil", "power oil", "mammador")),
        CatalogEntry("Salt", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.PACK, listOf("iodized salt", "table salt", "dangote salt")),
        CatalogEntry("Seasoning Cubes", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.PACK, listOf("maggi", "knorr", "royco", "onga", "star cubes")),
        CatalogEntry("Curry Powder", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.SACHET, listOf("curry", "lion curry")),
        CatalogEntry("Thyme", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.SACHET, listOf("dry thyme")),
        CatalogEntry("Egusi", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.MUDU, listOf("melon seeds", "ground egusi")),
        CatalogEntry("Ogbono", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.MUDU, listOf("apon", "bush mango seed", "ground ogbono")),
        CatalogEntry("Tomato Paste", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.SACHET, listOf("tin tomato", "gino", "de rica", "sachet tomato")),
        CatalogEntry("Iru", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.PACK, listOf("locust beans", "dawadawa")),
        CatalogEntry("Dry Pepper", GroceryCategory.COOKING_INGREDIENTS, NigerianUnits.PACK, listOf("cameroon pepper", "chili powder")),

        // 8. Beverages
        CatalogEntry("Milo", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("bournvita", "cocoa drink", "ovaltine")),
        CatalogEntry("Tea", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("lipton", "green tea", "black tea")),
        CatalogEntry("Coffee", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("nescafe")),
        CatalogEntry("Zobo", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("hibiscus flowers", "zobo leaves")),
        CatalogEntry("Malt", GroceryCategory.BEVERAGES, NigerianUnits.BOTTLE, listOf("malta guinness", "amstel malt", "maltex")),
        CatalogEntry("Soft Drinks", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("coke", "fanta", "sprite", "pepsi", "seven up")),
        CatalogEntry("Bottled Water", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("swan water", "eva water", "nestle water", "pure water", "sachet water")),
        CatalogEntry("Fruit Juice", GroceryCategory.BEVERAGES, NigerianUnits.PACK, listOf("chivita", "5 alive", "chi exotic")),

        // 9. Snacks & Confectionery
        CatalogEntry("Chin Chin", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.PACK, listOf("chinchin")),
        CatalogEntry("Plantain Chips", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.PACK),
        CatalogEntry("Groundnuts", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.BOTTLE, listOf("peanuts", "roasted groundnuts")),
        CatalogEntry("Cashew Nuts", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.BOTTLE),
        CatalogEntry("Biscuits", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.PACK, listOf("coaster", "digestive", "shortcake", "cookies")),
        CatalogEntry("Chocolates", GroceryCategory.SNACKS_AND_CONFECTIONERY, NigerianUnits.PACK, listOf("candy", "sweets", "tomtom")),

        // 10. Frozen Foods
        CatalogEntry("Frozen Chicken", GroceryCategory.FROZEN_FOODS, NigerianUnits.CARTON, listOf("frozen chicken wings")),
        CatalogEntry("Frozen Fish", GroceryCategory.FROZEN_FOODS, NigerianUnits.CARTON),
        CatalogEntry("Ice Cream", GroceryCategory.FROZEN_FOODS, NigerianUnits.PACK, listOf("fanice")),
        CatalogEntry("Ice", GroceryCategory.FROZEN_FOODS, NigerianUnits.BAG, listOf("ice block")),

        // 11. Personal Care
        CatalogEntry("Bathing Soap", GroceryCategory.PERSONAL_CARE, NigerianUnits.PACK, listOf("dudu osun", "premier", "lux", "soap bar")),
        CatalogEntry("Toothpaste", GroceryCategory.PERSONAL_CARE, NigerianUnits.PACK, listOf("close up", "oral b", "macleans")),
        CatalogEntry("Toothbrush", GroceryCategory.PERSONAL_CARE, NigerianUnits.PIECE),
        CatalogEntry("Body Lotion", GroceryCategory.PERSONAL_CARE, NigerianUnits.BOTTLE, listOf("body cream", "shea butter", "ori", "vaseline")),
        CatalogEntry("Shampoo", GroceryCategory.PERSONAL_CARE, NigerianUnits.BOTTLE, listOf("hair conditioner")),
        CatalogEntry("Sanitary Products", GroceryCategory.PERSONAL_CARE, NigerianUnits.PACK, listOf("sanitary pads", "always", "kotex")),
        CatalogEntry("Deodorant", GroceryCategory.PERSONAL_CARE, NigerianUnits.BOTTLE, listOf("roll on", "body spray")),

        // 12. Cleaning Supplies
        CatalogEntry("Detergent", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.BAG, listOf("omo", "ariel", "sunlight", "klin", "viva", "waw")),
        CatalogEntry("Dishwashing Liquid", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.BOTTLE, listOf("morning fresh", "mama lemon", "dish soap")),
        CatalogEntry("Bleach", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.BOTTLE, listOf("jik", "hypo")),
        CatalogEntry("Disinfectant", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.BOTTLE, listOf("dettol", "izal", "zgerm")),
        CatalogEntry("Sponge", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.PACK, listOf("iron sponge", "scouring pad")),
        CatalogEntry("Toilet Cleaner", GroceryCategory.CLEANING_SUPPLIES, NigerianUnits.BOTTLE, listOf("harpic", "toilet wash")),

        // 13. Household Essentials
        CatalogEntry("Tissue Paper", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.PACK, listOf("toilet roll", "serviette", "kitchen towel")),
        CatalogEntry("Trash Bags", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.PACK, listOf("garbage bags", "waste bags")),
        CatalogEntry("Matches", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.PACK, listOf("matchbox", "lighter")),
        CatalogEntry("Batteries", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.PACK, listOf("tiger batteries", "aa batteries")),
        CatalogEntry("Light Bulbs", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.PIECE, listOf("led bulb", "rechargeable bulb")),
        CatalogEntry("Insecticide", GroceryCategory.HOUSEHOLD_ESSENTIALS, NigerianUnits.BOTTLE, listOf("baygon", "raid", "rambo", "sniper"))
    )

    fun findMatching(query: String): List<CatalogEntry> {
        val clean = query.trim().lowercase()
        if (clean.isBlank()) return emptyList()

        return entries.filter { entry ->
            entry.canonicalName.lowercase().contains(clean) ||
            entry.aliases.any { it.lowercase().contains(clean) } ||
            entry.searchKeywords.any { it.lowercase().contains(clean) }
        }.take(8)
    }
}
