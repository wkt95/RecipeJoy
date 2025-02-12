package com.example.recipejoy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val unit: String,
    val recipeId: Int = 0
) : Parcelable