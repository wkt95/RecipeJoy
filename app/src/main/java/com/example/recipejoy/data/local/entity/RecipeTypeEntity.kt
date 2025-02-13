package com.example.recipejoy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_types")
data class RecipeTypeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String
)