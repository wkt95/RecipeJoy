package com.example.recipejoy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipejoy.data.local.entity.RecipeTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeTypeDao {
    @Query("SELECT * FROM recipe_types ORDER BY name ASC")
    fun getAllRecipeTypes(): Flow<List<RecipeTypeEntity>>

    @Query("SELECT * FROM recipe_types WHERE id = :typeId")
    suspend fun getRecipeTypeById(typeId: Int): RecipeTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeTypes(types: List<RecipeTypeEntity>)

    @Query("SELECT COUNT(*) FROM recipe_types")
    suspend fun getRecipeTypesCount(): Int
}