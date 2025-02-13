package com.example.recipejoy.utils

import android.content.Context
import com.example.recipejoy.data.local.dao.RecipeDao
import com.example.recipejoy.data.local.dao.RecipeTypeDao
import com.example.recipejoy.data.local.entity.RecipeEntity
import com.example.recipejoy.data.local.entity.IngredientEntity
import com.example.recipejoy.data.local.entity.InstructionEntity
import com.example.recipejoy.R
import com.example.recipejoy.data.local.entity.RecipeTypeEntity
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class RecipeTypeJson(
    @Json(name = "recipeTypes")
    val types: List<RecipeTypeEntity>
)

@JsonClass(generateAdapter = true)
data class RecipeJson(
    val title: String,
    val description: String,
    val cookingTime: Int,
    val servings: Int,
    val typeId: Int,
    val imagePath: String?,
    val ingredients: List<IngredientJson>,
    val instructions: List<String>
)

@JsonClass(generateAdapter = true)
data class IngredientJson(
    val name: String,
    val amount: Double,
    val unit: String
)

@JsonClass(generateAdapter = true)
data class RecipesJson(
    val recipes: List<RecipeJson>
)

class DatabaseLoader(
    private val context: Context,
    private val recipeTypeDao: RecipeTypeDao,
    private val recipeDao: RecipeDao
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun preloadData() {
        withContext(Dispatchers.IO) {
            val recipeCount = recipeDao.getRecipeCount()
            println("Current recipe count before preload: $recipeCount")

            if (recipeCount == 0) {
                preloadRecipeTypes()
                preloadRecipes()
            }
        }
    }

    suspend fun preloadRecipeTypes() {
        if (recipeTypeDao.getRecipeTypesCount() == 0) {
            try {
                val jsonString = context.resources
                    .openRawResource(R.raw.recipetypes)
                    .bufferedReader()
                    .use { it.readText() }

                val adapter = moshi.adapter(RecipeTypeJson::class.java)
                val recipeTypes = adapter.fromJson(jsonString)?.types

                recipeTypes?.let {
                    recipeTypeDao.insertRecipeTypes(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun preloadRecipes() {
        println("Starting preload recipes")
        if (recipeDao.getRecipeCount() == 0) {
            try {
                val jsonString = context.resources
                    .openRawResource(R.raw.recipes)
                    .bufferedReader()
                    .use { it.readText() }

                println("Recipe JSON content: $jsonString")

                val adapter = moshi.adapter(RecipesJson::class.java)
                val recipesJson = adapter.fromJson(jsonString)?.recipes

                println("Parsed recipes: ${recipesJson?.size}")
                println("First recipe image path: ${recipesJson?.firstOrNull()?.imagePath}")

                recipesJson?.forEach { recipeJson ->
                    val recipe = RecipeEntity(
                        title = recipeJson.title,
                        description = recipeJson.description,
                        cookingTime = recipeJson.cookingTime,
                        servings = recipeJson.servings,
                        typeId = recipeJson.typeId,
                        imagePath = recipeJson.imagePath
                    )

                    val ingredients = recipeJson.ingredients.map { ingredient ->
                        IngredientEntity(
                            name = ingredient.name,
                            amount = ingredient.amount,
                            unit = ingredient.unit,
                            recipeId = 0
                        )
                    }

                    val instructions = recipeJson.instructions.mapIndexed { index, instruction ->
                        InstructionEntity(
                            stepNumber = index + 1,
                            instruction = instruction,
                            recipeId = 0
                        )
                    }

                    recipeDao.insertRecipeWithDetails(recipe, ingredients, instructions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error loading recipes: ${e.message}")
            }
        }
    }
}