package com.example.recipejoy.data.repository

import com.example.recipejoy.data.local.dao.RecipeTypeDao
import com.example.recipejoy.data.model.RecipeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeTypeRepository(
    private val recipeTypeDao: RecipeTypeDao
) {
    fun getAllRecipeTypes(): Flow<List<RecipeType>> {
        return recipeTypeDao.getAllRecipeTypes().map { entities ->
            entities.map { entity ->
                RecipeType(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description
                )
            }
        }
    }

    suspend fun getRecipeTypeById(typeId: Int): RecipeType? {
        return recipeTypeDao.getRecipeTypeById(typeId)?.let { entity ->
            RecipeType(
                id = entity.id,
                name = entity.name,
                description = entity.description
            )
        }
    }
}