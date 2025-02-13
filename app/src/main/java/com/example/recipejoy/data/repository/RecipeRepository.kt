package com.example.recipejoy.data.repository

import com.example.recipejoy.data.local.dao.RecipeDao
import com.example.recipejoy.data.local.entity.IngredientEntity
import com.example.recipejoy.data.local.entity.InstructionEntity
import com.example.recipejoy.data.local.entity.RecipeEntity
import com.example.recipejoy.data.model.Ingredient
import com.example.recipejoy.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao
) {
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { recipeWithDetailsList ->
            recipeWithDetailsList.map { recipeWithDetails ->
                Recipe(
                    id = recipeWithDetails.recipe.id,
                    title = recipeWithDetails.recipe.title,
                    description = recipeWithDetails.recipe.description,
                    ingredients = recipeWithDetails.ingredients.map { ingredientEntity ->
                        Ingredient(
                            id = ingredientEntity.id,
                            name = ingredientEntity.name,
                            amount = ingredientEntity.amount,
                            unit = ingredientEntity.unit,
                            recipeId = ingredientEntity.recipeId
                        )
                    },
                    instructions = recipeWithDetails.instructions
                        .sortedBy { it.stepNumber }
                        .map { it.instruction },
                    cookingTime = recipeWithDetails.recipe.cookingTime,
                    servings = recipeWithDetails.recipe.servings,
                    typeId = recipeWithDetails.recipe.typeId,
                    imagePath = recipeWithDetails.recipe.imagePath,
                    isFavorite = recipeWithDetails.recipe.isFavorite,
                    createdAt = recipeWithDetails.recipe.createdAt
                )
            }
        }
    }

    fun getRecipesByType(typeId: Int): Flow<List<Recipe>> {
        return recipeDao.getRecipesByType(typeId).map { recipeWithDetailsList ->
            recipeWithDetailsList.map { recipeWithDetails ->
                Recipe(
                    id = recipeWithDetails.recipe.id,
                    title = recipeWithDetails.recipe.title,
                    description = recipeWithDetails.recipe.description,
                    ingredients = recipeWithDetails.ingredients.map { ingredientEntity ->
                        Ingredient(
                            id = ingredientEntity.id,
                            name = ingredientEntity.name,
                            amount = ingredientEntity.amount,
                            unit = ingredientEntity.unit,
                            recipeId = ingredientEntity.recipeId
                        )
                    },
                    instructions = recipeWithDetails.instructions
                        .sortedBy { it.stepNumber }
                        .map { it.instruction },
                    cookingTime = recipeWithDetails.recipe.cookingTime,
                    servings = recipeWithDetails.recipe.servings,
                    typeId = recipeWithDetails.recipe.typeId,
                    imagePath = recipeWithDetails.recipe.imagePath,
                    isFavorite = recipeWithDetails.recipe.isFavorite,
                    createdAt = recipeWithDetails.recipe.createdAt
                )
            }
        }
    }

    fun getRecipeById(recipeId: Int): Flow<Recipe?> {
        return recipeDao.getRecipeById(recipeId).map { recipeWithDetails ->
            println("Fetched recipe: ${recipeWithDetails?.recipe?.title}")
            println("Instructions count: ${recipeWithDetails?.instructions?.size}")
            recipeWithDetails?.let {
                Recipe(
                    id = it.recipe.id,
                    title = it.recipe.title,
                    description = it.recipe.description,
                    ingredients = it.ingredients.map { ingredientEntity ->
                        Ingredient(
                            id = ingredientEntity.id,
                            name = ingredientEntity.name,
                            amount = ingredientEntity.amount,
                            unit = ingredientEntity.unit,
                            recipeId = ingredientEntity.recipeId
                        )
                    },
                    instructions = it.instructions
                        .sortedBy { instruction -> instruction.stepNumber }
                        .map { instruction -> instruction.instruction },
                    cookingTime = it.recipe.cookingTime,
                    servings = it.recipe.servings,
                    typeId = it.recipe.typeId,
                    imagePath = it.recipe.imagePath,
                    isFavorite = it.recipe.isFavorite,
                    createdAt = it.recipe.createdAt
                )
            }
        }
    }

    suspend fun insertRecipe(recipe: Recipe) {
        val recipeEntity = RecipeEntity(
            title = recipe.title,
            description = recipe.description,
            cookingTime = recipe.cookingTime,
            servings = recipe.servings,
            typeId = recipe.typeId,
            imagePath = recipe.imagePath,
            isFavorite = recipe.isFavorite
        )

        val ingredients = recipe.ingredients.mapIndexed { index, ingredient ->
            IngredientEntity(
                name = ingredient.name,
                amount = ingredient.amount,
                unit = ingredient.unit,
                recipeId = recipe.id
            )
        }

        val instructions = recipe.instructions.mapIndexed { index, instruction ->
            InstructionEntity(
                stepNumber = index + 1,
                instruction = instruction,
                recipeId = recipe.id
            )
        }

        recipeDao.insertRecipeWithDetails(recipeEntity, ingredients, instructions)
    }

    suspend fun updateRecipe(recipe: Recipe) {

        recipeDao.deleteRecipeById(recipe.id)
        println("Updating recipe with instructions count: ${recipe.instructions.size}")

        val recipeEntity = RecipeEntity(
            id = recipe.id,
            title = recipe.title,
            description = recipe.description,
            cookingTime = recipe.cookingTime,
            servings = recipe.servings,
            typeId = recipe.typeId,
            imagePath = recipe.imagePath
        )

        val ingredients = recipe.ingredients.map { ingredient ->
            IngredientEntity(
                name = ingredient.name,
                amount = ingredient.amount,
                unit = ingredient.unit,
                recipeId = recipe.id
            )
        }

        val instructions = recipe.instructions.distinct().mapIndexed { index, instruction ->
            InstructionEntity(
                stepNumber = index + 1,
                instruction = instruction,
                recipeId = recipe.id
            )
        }

        recipeDao.insertRecipeWithDetails(
            recipe = recipeEntity,
            ingredients = ingredients,
            instructions = instructions
        )
    }

    suspend fun deleteRecipe(recipeId: Int) {
        recipeDao.deleteRecipeById(recipeId)
    }

    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }
}