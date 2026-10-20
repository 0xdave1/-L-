package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.CreateListDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListDetailScreen
import com.example.ui.screens.MyListsScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.viewmodel.GroceryUiEvent
import com.example.ui.viewmodel.GroceryViewModel
import kotlinx.coroutines.flow.collectLatest

enum class BottomNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_home"),
    LISTS("My Lists", Icons.Default.List, Icons.Default.List, "bottom_nav_lists"),
    FAVORITES("Favorites", Icons.Filled.Star, Icons.Outlined.StarBorder, "bottom_nav_favorites"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "bottom_nav_history")
}

@Composable
fun MainAppScaffold(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val selectedListId by viewModel.selectedListId.collectAsStateWithLifecycle()
    val showCreateListDialog by viewModel.showCreateListDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(BottomNavDestination.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is GroceryUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed && event.actionLabel == "Undo") {
                        viewModel.undoClearPurchased()
                    }
                }
                is GroceryUiEvent.NavigateToList -> {
                    viewModel.selectList(event.listId)
                }
            }
        }
    }

    // Handle back button when viewing a specific list
    if (selectedListId != null) {
        BackHandler {
            viewModel.selectList(null)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (selectedListId == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    BottomNavDestination.values().forEach { destination ->
                        val isSelected = currentTab == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = destination },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedListId != null) {
                ListDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectList(null) }
                )
            } else {
                when (currentTab) {
                    BottomNavDestination.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToList = { listId -> viewModel.selectList(listId) }
                    )
                    BottomNavDestination.LISTS -> MyListsScreen(
                        viewModel = viewModel,
                        onNavigateToList = { listId -> viewModel.selectList(listId) }
                    )
                    BottomNavDestination.FAVORITES -> FavoritesScreen(
                        viewModel = viewModel
                    )
                    BottomNavDestination.HISTORY -> HistoryScreen(
                        viewModel = viewModel,
                        onNavigateToList = { listId -> viewModel.selectList(listId) }
                    )
                }
            }
        }
    }

    // Create List Dialog
    if (showCreateListDialog) {
        CreateListDialog(
            initialCurrency = currentCurrency,
            onConfirm = { name, budget, currencyCode ->
                viewModel.createNewList(name, budget, currencyCode)
            },
            onDismiss = { viewModel.showCreateListDialog.value = false }
        )
    }

    // Settings & Currency Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showSettingsDialog.value = false }
        )
    }
}
