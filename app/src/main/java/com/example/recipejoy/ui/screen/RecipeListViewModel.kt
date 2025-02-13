package com.example.recipejoy.ui.screen.recipe.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipejoy.data.model.Recipe
import com.example.recipejoy.data.model.RecipeType
import com.example.recipejoy.data.repository.RecipeRepository
import com.example.recipejoy.data.repository.RecipeTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val recipeTypeRepository: RecipeTypeRepository
) : ViewModel() {

    private val _selectedTypeId = MutableStateFlow<Int?>(null)
    private val _searchQuery = MutableStateFlow("")

    val recipeTypes: StateFlow<List<RecipeType>> = recipeTypeRepository
        .getAllRecipeTypes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recipes: StateFlow<List<Recipe>> = combine(
        _selectedTypeId,
        _searchQuery,
        recipeRepository.getAllRecipes()
    ) { selectedTypeId, query, allRecipes ->
        allRecipes
            .filter { recipe ->
                (selectedTypeId == null || recipe.typeId == selectedTypeId) &&
                        (query.isEmpty() || recipe.title.contains(query, ignoreCase = true))
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectRecipeType(typeId: Int?) {
        _selectedTypeId.value = typeId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            recipeRepository.updateFavoriteStatus(recipeId, isFavorite)
        }
    }
}