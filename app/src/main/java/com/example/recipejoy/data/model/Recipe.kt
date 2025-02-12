package com.example.recipejoy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int = 0,
    val title: String,
    val description: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val cookingTime: Int,
    val servings: Int,
    val typeId: Int,
    val imagePath: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable