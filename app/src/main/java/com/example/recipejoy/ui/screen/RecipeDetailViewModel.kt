package com.example.recipejoy.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipejoy.data.model.Recipe
import com.example.recipejoy.data.model.RecipeType
import com.example.recipejoy.data.repository.RecipeRepository
import com.example.recipejoy.data.repository.RecipeTypeRepository
import com.example.recipejoy.utils.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val recipeTypeRepository: RecipeTypeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Int = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            combine(
                recipeRepository.getRecipeById(recipeId),
                recipeTypeRepository.getAllRecipeTypes()
            ) { recipe, types ->
                recipe to types
            }.collect { (recipe, types) ->
                recipe?.let { nonNullRecipe ->
                    val recipeType = types.find { it.id == nonNullRecipe.typeId }
                    _uiState.update {
                        it.copy(
                            recipe = nonNullRecipe,
                            recipeType = recipeType,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            recipeRepository.updateFavoriteStatus(
                recipeId = currentRecipe.id,
                isFavorite = !currentRecipe.isFavorite
            )
        }
    }

    fun deleteRecipe() {
        viewModelScope.launch {
            uiState.value.recipe?.imagePath?.let { path ->
                ImageUtils.deleteImage(path)
            }
            recipeRepository.deleteRecipe(recipeId)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val recipeType: RecipeType? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)