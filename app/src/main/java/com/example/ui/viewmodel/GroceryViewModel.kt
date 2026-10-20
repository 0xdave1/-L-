package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.classifier.CatalogEntry
import com.example.data.classifier.CategoryClassifier
import com.example.data.classifier.NigerianGroceryCatalog
import com.example.data.database.GroceryDatabase
import com.example.data.entity.FavoriteItem
import com.example.data.entity.GroceryItem
import com.example.data.entity.ItemUsage
import com.example.data.entity.ShoppingList
import com.example.data.model.Currencies
import com.example.data.model.CurrencyConfig
import com.example.data.model.GroceryCategory
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class GroceryUiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : GroceryUiEvent()
    data class NavigateToList(val listId: Long) : GroceryUiEvent()
}

enum class ItemFilter {
    ALL, REMAINING, PURCHASED
}

data class DuplicatePromptData(
    val listId: Long,
    val existingItem: GroceryItem,
    val candidateName: String,
    val candidateQuantity: Double,
    val candidateUnit: String?,
    val candidatePrice: Long?,
    val candidateCategory: GroceryCategory?
)

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GroceryRepository
    private var lastClearedPurchasedItems: List<GroceryItem> = emptyList()

    private val _eventFlow = MutableSharedFlow<GroceryUiEvent>()
    val eventFlow: SharedFlow<GroceryUiEvent> = _eventFlow.asSharedFlow()

    // Global selected list id for detail screen
    private val _selectedListId = MutableStateFlow<Long?>(null)
    val selectedListId = _selectedListId.asStateFlow()

    // Current detail screen search & filter states
    val searchQuery = MutableStateFlow("")
    val itemFilter = MutableStateFlow(ItemFilter.ALL)
    val categoryFilter = MutableStateFlow<GroceryCategory?>(null)

    // Quick add text and live autocomplete
    val inputText = MutableStateFlow("")
    val selectedCategoryOverride = MutableStateFlow<GroceryCategory?>(null)

    // Dialog & Prompt states
    val duplicatePrompt = MutableStateFlow<DuplicatePromptData?>(null)
    val editingItem = MutableStateFlow<GroceryItem?>(null)
    val showCreateListDialog = MutableStateFlow(false)
    val showEditBudgetDialog = MutableStateFlow(false)
    val showClearConfirmDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val showAddItemDialog = MutableStateFlow(false)
    val addItemPrefillName = MutableStateFlow("")

    // Currency selection
    val currentCurrency = MutableStateFlow(Currencies.NGN)

    init {
        val database = GroceryDatabase.getDatabase(application, viewModelScope)
        repository = GroceryRepository(database)
    }

    val allLists: StateFlow<List<ShoppingList>> = repository.allLists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLists: StateFlow<List<ShoppingList>> = repository.activeLists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedLists: StateFlow<List<ShoppingList>> = repository.completedLists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteItem>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val frequentlyBought: StateFlow<List<ItemUsage>> = repository.frequentlyBought
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently opened shopping list
    val currentShoppingList: StateFlow<ShoppingList?> = _selectedListId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getList(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Items for current list
    val rawCurrentItems: StateFlow<List<GroceryItem>> = _selectedListId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getItems(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Autocomplete suggestions based on input
    val autocompleteSuggestions: StateFlow<List<CatalogEntry>> = inputText
        .combine(_selectedListId) { text, _ ->
            if (text.length >= 2) NigerianGroceryCatalog.findMatching(text) else emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live auto-categorization detection while typing
    val detectedCategory: StateFlow<GroceryCategory> = combine(
        inputText,
        selectedCategoryOverride,
        repository.userCategoryRules
    ) { text, overrideCat, rules ->
        if (overrideCat != null) return@combine overrideCat
        if (text.isBlank()) return@combine GroceryCategory.OTHER
        val rulesMap = rules.associate { it.normalizedItemName to it.categoryId }
        CategoryClassifier.classify(text, rulesMap)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroceryCategory.OTHER)

    fun selectList(listId: Long?) {
        _selectedListId.value = listId
        searchQuery.value = ""
        itemFilter.value = ItemFilter.ALL
        categoryFilter.value = null
        inputText.value = ""
        selectedCategoryOverride.value = null
    }

    fun onInputTextChanged(text: String) {
        inputText.value = text
        if (text.isBlank()) {
            selectedCategoryOverride.value = null
        }
    }

    fun onCategoryOverrideSelected(category: GroceryCategory?) {
        selectedCategoryOverride.value = category
    }

    fun createNewList(name: String, budgetMinorUnits: Long? = null, currencyCode: String = "NGN") {
        viewModelScope.launch {
            val newId = repository.createList(
                name = name,
                budgetMinorUnits = budgetMinorUnits,
                currencyCode = currencyCode
            )
            showCreateListDialog.value = false
            _eventFlow.emit(GroceryUiEvent.NavigateToList(newId))
        }
    }

    fun renameList(listId: Long, newName: String) {
        viewModelScope.launch {
            repository.renameList(listId, newName)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("List renamed"))
        }
    }

    fun updateListBudget(listId: Long, budgetMinorUnits: Long?, currencyCode: String) {
        viewModelScope.launch {
            repository.updateBudget(listId, budgetMinorUnits, currencyCode)
            showEditBudgetDialog.value = false
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Budget updated"))
        }
    }

    fun duplicateList(listId: Long) {
        viewModelScope.launch {
            val newId = repository.duplicateList(listId)
            if (newId > 0) {
                _eventFlow.emit(GroceryUiEvent.ShowSnackbar("List duplicated for new shopping trip"))
                _eventFlow.emit(GroceryUiEvent.NavigateToList(newId))
            }
        }
    }

    fun markListCompleted(listId: Long) {
        viewModelScope.launch {
            repository.markListCompleted(listId)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Shopping trip marked as completed!"))
        }
    }

    fun reopenList(listId: Long) {
        viewModelScope.launch {
            repository.reopenList(listId)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("List reopened for shopping"))
        }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch {
            repository.deleteList(listId)
            if (_selectedListId.value == listId) {
                _selectedListId.value = null
            }
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Shopping list deleted"))
        }
    }

    fun openAddItemDialog(prefill: String = "") {
        addItemPrefillName.value = prefill
        showAddItemDialog.value = true
    }

    fun closeAddItemDialog() {
        showAddItemDialog.value = false
        addItemPrefillName.value = ""
    }

    fun recordActualPrice(item: GroceryItem, actualLineTotalMinorUnits: Long) {
        viewModelScope.launch {
            repository.updateItem(
                item.copy(
                    actualLineTotalMinorUnits = actualLineTotalMinorUnits,
                    useActualLineTotal = true,
                    isPurchased = true
                )
            )
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Recorded spending for ${item.name}"))
        }
    }

    fun attemptAddItem(
        name: String,
        quantity: Double = 1.0,
        unit: String? = null,
        estimatedPriceMinorUnits: Long? = null,
        category: GroceryCategory? = null,
        notes: String = ""
    ) {
        val listId = _selectedListId.value ?: return
        val cleanName = name.trim()
        if (cleanName.isBlank()) return

        viewModelScope.launch {
            // Check for existing item with duplicate prevention
            val existing = repository.checkExistingItem(listId, cleanName)
            if (existing != null) {
                duplicatePrompt.value = DuplicatePromptData(
                    listId = listId,
                    existingItem = existing,
                    candidateName = cleanName,
                    candidateQuantity = quantity,
                    candidateUnit = unit,
                    candidatePrice = estimatedPriceMinorUnits,
                    candidateCategory = category
                )
            } else {
                executeAddItem(
                    listId = listId,
                    name = cleanName,
                    quantity = quantity,
                    unit = unit,
                    estimatedPriceMinorUnits = estimatedPriceMinorUnits,
                    category = category,
                    notes = notes
                )
            }
        }
    }

    fun resolveDuplicateIncreaseQuantity() {
        val prompt = duplicatePrompt.value ?: return
        viewModelScope.launch {
            val newQty = prompt.existingItem.quantity + prompt.candidateQuantity
            repository.updateQuantity(prompt.existingItem.id, newQty)
            // If it was checked off, uncheck so it's active again
            if (prompt.existingItem.isPurchased) {
                repository.toggleItemPurchased(prompt.existingItem)
            }
            duplicatePrompt.value = null
            inputText.value = ""
            selectedCategoryOverride.value = null
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Increased quantity of ${prompt.candidateName}"))
        }
    }

    fun resolveDuplicateKeepSeparate() {
        val prompt = duplicatePrompt.value ?: return
        viewModelScope.launch {
            executeAddItem(
                listId = prompt.listId,
                name = prompt.candidateName,
                quantity = prompt.candidateQuantity,
                unit = prompt.candidateUnit,
                estimatedPriceMinorUnits = prompt.candidatePrice,
                category = prompt.candidateCategory
            )
            duplicatePrompt.value = null
        }
    }

    fun dismissDuplicatePrompt() {
        duplicatePrompt.value = null
    }

    private suspend fun executeAddItem(
        listId: Long,
        name: String,
        quantity: Double,
        unit: String?,
        estimatedPriceMinorUnits: Long?,
        category: GroceryCategory?,
        notes: String = ""
    ) {
        repository.addItem(
            listId = listId,
            name = name,
            quantity = quantity,
            unit = unit,
            estimatedUnitPriceMinorUnits = estimatedPriceMinorUnits,
            categoryOverride = category ?: selectedCategoryOverride.value,
            notes = notes
        )
        inputText.value = ""
        selectedCategoryOverride.value = null
        _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Added $name"))
    }

    fun togglePurchased(item: GroceryItem) {
        viewModelScope.launch {
            repository.toggleItemPurchased(item)
        }
    }

    fun adjustQuantity(item: GroceryItem, delta: Double) {
        viewModelScope.launch {
            val newQty = (item.quantity + delta).coerceAtLeast(0.0)
            repository.updateQuantity(item.id, newQty)
        }
    }

    fun updateItemDetails(updatedItem: GroceryItem) {
        viewModelScope.launch {
            repository.updateItem(updatedItem)
            editingItem.value = null
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Updated ${updatedItem.name}"))
        }
    }

    fun changeItemCategory(item: GroceryItem, newCategory: GroceryCategory, remember: Boolean = true) {
        viewModelScope.launch {
            repository.updateCategory(item.id, item.name, newCategory, remember)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Moved to ${newCategory.displayName}"))
        }
    }

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.deleteItem(item.id)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Removed ${item.name}"))
        }
    }

    fun clearPurchasedItems() {
        val listId = _selectedListId.value ?: return
        viewModelScope.launch {
            val cleared = repository.clearPurchasedItems(listId)
            showClearConfirmDialog.value = false
            if (cleared.isNotEmpty()) {
                lastClearedPurchasedItems = cleared
                _eventFlow.emit(
                    GroceryUiEvent.ShowSnackbar(
                        message = "Cleared ${cleared.size} purchased item${if (cleared.size > 1) "s" else ""}",
                        actionLabel = "Undo"
                    )
                )
            }
        }
    }

    fun undoClearPurchased() {
        if (lastClearedPurchasedItems.isNotEmpty()) {
            viewModelScope.launch {
                repository.restoreItems(lastClearedPurchasedItems)
                lastClearedPurchasedItems = emptyList()
            }
        }
    }

    fun toggleFavorite(canonicalName: String, categoryId: String, unit: String) {
        viewModelScope.launch {
            repository.toggleFavorite(canonicalName, categoryId, unit)
        }
    }

    fun deleteFavorite(id: Long) {
        viewModelScope.launch {
            repository.deleteFavorite(id)
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Removed from Favorites"))
        }
    }

    fun addFavoriteToList(targetListId: Long, favorite: FavoriteItem) {
        viewModelScope.launch {
            repository.addItem(
                listId = targetListId,
                name = favorite.canonicalName,
                quantity = favorite.defaultQuantity,
                unit = favorite.defaultUnit,
                categoryOverride = favorite.categoryEnum
            )
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Added ${favorite.canonicalName} to list"))
        }
    }

    fun addFrequentItemToList(targetListId: Long, usage: ItemUsage) {
        viewModelScope.launch {
            repository.addItem(
                listId = targetListId,
                name = usage.displayName,
                quantity = 1.0,
                unit = CategoryClassifier.suggestUnit(usage.displayName),
                categoryOverride = usage.categoryEnum
            )
            _eventFlow.emit(GroceryUiEvent.ShowSnackbar("Added ${usage.displayName} to list"))
        }
    }

    fun setCurrency(currency: CurrencyConfig) {
        currentCurrency.value = currency
    }
}
