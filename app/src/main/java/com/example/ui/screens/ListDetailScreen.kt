package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GroceryItem
import com.example.data.entity.ShoppingList
import com.example.data.model.Currencies
import com.example.data.model.GroceryCategory
import com.example.ui.components.AddItemDialog
import com.example.ui.components.AutocompleteQuickAddBar
import com.example.ui.components.BudgetSummaryCard
import com.example.ui.components.CategorySectionHeader
import com.example.ui.components.ConfirmClearDialog
import com.example.ui.components.DuplicateItemDialog
import com.example.ui.components.EditBudgetDialog
import com.example.ui.components.EditItemDialog
import com.example.ui.components.GroceryItemRow
import com.example.ui.components.QuickSuggestionsRow
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.GroceryViewModel
import com.example.ui.viewmodel.ItemFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentList by viewModel.currentShoppingList.collectAsStateWithLifecycle()
    val allItems by viewModel.rawCurrentItems.collectAsStateWithLifecycle()
    val suggestions by viewModel.autocompleteSuggestions.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val detectedCategory by viewModel.detectedCategory.collectAsStateWithLifecycle()
    val selectedCategoryOverride by viewModel.selectedCategoryOverride.collectAsStateWithLifecycle()
    val frequentlyBought by viewModel.frequentlyBought.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val itemFilter by viewModel.itemFilter.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()

    val duplicatePrompt by viewModel.duplicatePrompt.collectAsStateWithLifecycle()
    val editingItem by viewModel.editingItem.collectAsStateWithLifecycle()
    val showEditBudgetDialog by viewModel.showEditBudgetDialog.collectAsStateWithLifecycle()
    val showClearConfirmDialog by viewModel.showClearConfirmDialog.collectAsStateWithLifecycle()
    val showAddItemDialog by viewModel.showAddItemDialog.collectAsStateWithLifecycle()
    val addItemPrefillName by viewModel.addItemPrefillName.collectAsStateWithLifecycle()

    var showOptionsMenu by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Map to track collapsed categories
    val collapsedCategories = remember { mutableStateMapOf<String, Boolean>() }

    if (currentList == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading shopping list...")
        }
        return
    }

    val list = currentList!!
    val currency = Currencies.get(list.currencyCode)

    // Calculate metrics
    val estimatedTotal = allItems.sumOf { it.estimatedLineTotalMinorUnits }
    val actualSpending = allItems.mapNotNull { it.actualConfirmedLineTotalMinorUnits }.sum()
    val purchasedCount = allItems.count { it.isPurchased }

    // Filter items
    val filteredItems = allItems.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.notes.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (itemFilter) {
            ItemFilter.ALL -> true
            ItemFilter.REMAINING -> !item.isPurchased
            ItemFilter.PURCHASED -> item.isPurchased
        }

        val matchesCategory = categoryFilter == null || item.categoryEnum == categoryFilter

        matchesSearch && matchesStatus && matchesCategory
    }

    // Group by category sorted by GroceryCategory.sortOrder
    val groupedByCategory = filteredItems
        .groupBy { it.categoryEnum }
        .toSortedMap(compareBy { it.sortOrder })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = list.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = "${allItems.size} items • $purchasedCount purchased",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openAddItemDialog("") },
                        modifier = Modifier.testTag("top_add_item_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Item with Details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Item (Specify Qty & Price)") },
                                onClick = {
                                    viewModel.openAddItemDialog("")
                                    showOptionsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Budget") },
                                onClick = {
                                    viewModel.showEditBudgetDialog.value = true
                                    showOptionsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate List") },
                                onClick = {
                                    viewModel.duplicateList(list.id)
                                    showOptionsMenu = false
                                }
                            )
                            if (!list.isCompleted) {
                                DropdownMenuItem(
                                    text = { Text("Mark as Completed") },
                                    onClick = {
                                        viewModel.markListCompleted(list.id)
                                        showOptionsMenu = false
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Reopen List") },
                                    onClick = {
                                        viewModel.reopenList(list.id)
                                        showOptionsMenu = false
                                    }
                                )
                            }
                            if (purchasedCount > 0) {
                                DropdownMenuItem(
                                    text = { Text("Clear Finished Items", color = StatusError) },
                                    onClick = {
                                        viewModel.clearPurchasedItems()
                                        showOptionsMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // Bottom Action Area with One-Tap Clear Finished and Finish Shopping
            if (purchasedCount > 0 || allItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // One-Tap Clear Finished with instant Undo snackbar
                        if (purchasedCount > 0) {
                            Button(
                                onClick = { viewModel.clearPurchasedItems() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bottom_clear_finished_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clear Finished ($purchasedCount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!list.isCompleted) {
                            Button(
                                onClick = { viewModel.markListCompleted(list.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f).testTag("finish_shopping_trip_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Finish Trip",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Add Entry Bar with Autocomplete, Live Category Badge & Specify Details button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                AutocompleteQuickAddBar(
                    inputText = inputText,
                    detectedCategory = detectedCategory,
                    selectedCategoryOverride = selectedCategoryOverride,
                    suggestions = suggestions,
                    onInputTextChanged = viewModel::onInputTextChanged,
                    onCategoryOverrideSelected = viewModel::onCategoryOverrideSelected,
                    onAddItem = { itemName, catOverride ->
                        viewModel.attemptAddItem(name = itemName, category = catOverride)
                    },
                    onOpenDetailsDialog = { prefill ->
                        viewModel.openAddItemDialog(prefill)
                    }
                )
            }

            // Quick Suggestions Row (Favorites & Frequently Bought items)
            QuickSuggestionsRow(
                frequentlyBought = frequentlyBought,
                favorites = favorites,
                onAddFrequentItem = { usage -> viewModel.addFrequentItemToList(list.id, usage) },
                onAddFavoriteItem = { fav -> viewModel.addFavoriteToList(list.id, fav) }
            )

            // Search Bar if toggled
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Filter items in this list...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Filter Chips: All, To Buy, Purchased
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = itemFilter == ItemFilter.ALL,
                    onClick = { viewModel.itemFilter.value = ItemFilter.ALL },
                    label = { Text("All (${allItems.size})", fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp)
                )
                FilterChip(
                    selected = itemFilter == ItemFilter.REMAINING,
                    onClick = { viewModel.itemFilter.value = ItemFilter.REMAINING },
                    label = { Text("To Buy (${allItems.size - purchasedCount})", fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp)
                )
                FilterChip(
                    selected = itemFilter == ItemFilter.PURCHASED,
                    onClick = { viewModel.itemFilter.value = ItemFilter.PURCHASED },
                    label = { Text("Purchased ($purchasedCount)", fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Category Horizontal Filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = categoryFilter == null,
                        onClick = { viewModel.categoryFilter.value = null },
                        label = { Text("All Categories", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(GroceryCategory.values()) { cat ->
                    FilterChip(
                        selected = categoryFilter == cat,
                        onClick = { viewModel.categoryFilter.value = if (categoryFilter == cat) null else cat },
                        label = { Text("${cat.emoji} ${cat.displayName}", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Main List Area
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("list_items_lazy_column")
            ) {
                // Budget Card Header
                item(key = "budget_card") {
                    BudgetSummaryCard(
                        list = list,
                        estimatedTotalMinorUnits = estimatedTotal,
                        actualSpendingMinorUnits = actualSpending,
                        onEditBudgetClick = { viewModel.showEditBudgetDialog.value = true }
                    )
                }

                if (filteredItems.isEmpty()) {
                    item(key = "empty_items") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (allItems.isEmpty()) "Your shopping list is empty" else "No matching items",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (allItems.isEmpty()) "Type any item above, tap quick suggestions, or use + to specify quantities and prices." else "Try searching for another keyword.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Grouped by Category in aisle order
                    groupedByCategory.forEach { (category, itemsInCategory) ->
                        val isExpanded = collapsedCategories[category.id] != true

                        item(key = "cat_header_${category.id}") {
                            CategorySectionHeader(
                                category = category,
                                itemCount = itemsInCategory.size,
                                isExpanded = isExpanded,
                                onToggleExpand = {
                                    collapsedCategories[category.id] = !isExpanded
                                }
                            )
                        }

                        if (isExpanded) {
                            items(itemsInCategory, key = { it.id }) { item ->
                                GroceryItemRow(
                                    item = item,
                                    currencyCode = list.currencyCode,
                                    onTogglePurchased = viewModel::togglePurchased,
                                    onAdjustQuantity = viewModel::adjustQuantity,
                                    onItemClick = { viewModel.editingItem.value = it },
                                    onChangeCategory = { targetItem, newCat ->
                                        viewModel.changeItemCategory(targetItem, newCat, remember = true)
                                    },
                                    onDeleteItem = viewModel::deleteItem
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Item Dialog (Specify Quantity & Price)
    if (showAddItemDialog) {
        AddItemDialog(
            initialName = addItemPrefillName,
            currencyCode = list.currencyCode,
            onConfirm = { name, quantity, unit, estPrice, category, notes ->
                viewModel.attemptAddItem(
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    estimatedPriceMinorUnits = estPrice,
                    category = category,
                    notes = notes
                )
                viewModel.closeAddItemDialog()
            },
            onDismiss = { viewModel.closeAddItemDialog() }
        )
    }

    // Duplicate item resolution dialog
    duplicatePrompt?.let { prompt ->
        DuplicateItemDialog(
            data = prompt,
            onIncreaseQuantity = viewModel::resolveDuplicateIncreaseQuantity,
            onKeepSeparate = viewModel::resolveDuplicateKeepSeparate,
            onDismiss = viewModel::dismissDuplicatePrompt
        )
    }

    // Edit Item Details dialog
    editingItem?.let { item ->
        EditItemDialog(
            item = item,
            currencyCode = list.currencyCode,
            onSave = viewModel::updateItemDetails,
            onDelete = {
                viewModel.deleteItem(it)
                viewModel.editingItem.value = null
            },
            onDismiss = { viewModel.editingItem.value = null }
        )
    }

    // Edit Budget dialog
    if (showEditBudgetDialog) {
        EditBudgetDialog(
            list = list,
            onConfirm = { budget, currencyCode ->
                viewModel.updateListBudget(list.id, budget, currencyCode)
            },
            onDismiss = { viewModel.showEditBudgetDialog.value = false }
        )
    }

    // Clear Finished items confirmation dialog (if opened from menu)
    if (showClearConfirmDialog) {
        ConfirmClearDialog(
            itemCount = purchasedCount,
            onConfirm = viewModel::clearPurchasedItems,
            onDismiss = { viewModel.showClearConfirmDialog.value = false }
        )
    }
}
