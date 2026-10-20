package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MyLists : Screen("my_lists")
    object Favorites : Screen("favorites")
    object History : Screen("history")
    object ListDetail : Screen("list_detail/{listId}") {
        fun createRoute(listId: Long) = "list_detail/$listId"
    }
}
