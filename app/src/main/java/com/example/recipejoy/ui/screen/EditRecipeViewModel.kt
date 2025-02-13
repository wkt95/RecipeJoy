package com.example.recipejoy.ui.screen

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.recipejoy.data.model.Ingredient
import com.example.recipejoy.data.model.Recipe
import com.example.recipejoy.data.model.RecipeType
import com.example.recipejoy.data.repository.RecipeRepository
import com.example.recipejoy.data.repository.RecipeTypeRepository
import com.example.recipejoy.utils.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    application: Application,
    private val recipeRepository: RecipeRepository,
    private val recipeTypeRepository: RecipeTypeRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recipeId: Int = checkNotNull(savedStateHandle["recipeId"])
    private val _uiState = MutableStateFlow(EditRecipeUiState())
    val uiState: StateFlow<EditRecipeUiState> = _uiState.asStateFlow()

    val recipeTypes: StateFlow<List<RecipeType>> = recipeTypeRepository
        .getAllRecipeTypes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
                            title = nonNullRecipe.title,
                            description = nonNullRecipe.description,
                            cookingTime = nonNullRecipe.cookingTime,
                            servings = nonNullRecipe.servings,
                            selectedType = recipeType,
                            imagePath = nonNullRecipe.imagePath,
                            ingredients = nonNullRecipe.ingredients,
                            instructions = nonNullRecipe.instructions,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val imagePath = ImageUtils.saveImageToInternalStorage(getApplication(), uri)
            imagePath?.let { path ->
                _uiState.update { it.copy(imagePath = path) }
            }
        }
    }

    fun onImageRemoved() {
        _uiState.value.imagePath?.let { path ->
            ImageUtils.deleteImage(path)
        }
        _uiState.update { it.copy(imagePath = null) }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCookingTime(time: String) {
        val timeInt = time.toIntOrNull() ?: 0
        _uiState.update { it.copy(cookingTime = timeInt) }
    }

    fun updateServings(servings: String) {
        val servingsInt = servings.toIntOrNull() ?: 0
        _uiState.update { it.copy(servings = servingsInt) }
    }

    fun updateSelectedType(type: RecipeType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun addIngredient(name: String, amount: Double, unit: String) {
        val newIngredient = Ingredient(
            name = name,
            amount = amount,
            unit = unit,
            recipeId = recipeId
        )
        _uiState.update {
            it.copy(ingredients = it.ingredients + newIngredient)
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update {
            it.copy(ingredients = it.ingredients.filterIndexed { i, _ -> i != index })
        }
    }

    fun addInstruction(instruction: String) {
        _uiState.update {
            it.copy(instructions = it.instructions + instruction)
        }
    }

    fun removeInstruction(index: Int) {
        _uiState.update {
            it.copy(instructions = it.instructions.filterIndexed { i, _ -> i != index })
        }
    }

    fun saveRecipe() {
        val currentState = _uiState.value
        if (currentState.isValid()) {
            viewModelScope.launch {
                val recipe = Recipe(
                    id = recipeId,
                    title = currentState.title,
                    description = currentState.description,
                    ingredients = currentState.ingredients.map { it.copy(recipeId = recipeId) },
                    instructions = currentState.instructions.toList(),
                    cookingTime = currentState.cookingTime,
                    servings = currentState.servings,
                    typeId = currentState.selectedType?.id ?: 0,
                    imagePath = currentState.imagePath
                )
                recipeRepository.updateRecipe(recipe)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun updateImageUrl(url: String) {
        _uiState.update { it.copy(imagePath = url) }
    }
}

data class EditRecipeUiState(
    val title: String = "",
    val description: String = "",
    val cookingTime: Int = 0,
    val servings: Int = 0,
    val selectedType: RecipeType? = null,
    val imagePath: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
) {
    fun isValid(): Boolean {
        return title.isNotBlank() &&
                description.isNotBlank() &&
                cookingTime > 0 &&
                servings > 0 &&
                selectedType != null &&
                instructions.isNotEmpty() &&
                ingredients.isNotEmpty()

    }
}