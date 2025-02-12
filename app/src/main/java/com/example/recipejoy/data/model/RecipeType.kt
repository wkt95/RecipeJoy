package com.example.recipejoy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeType(
    val id: Int,
    val name: String,
    val description: String
) : Parcelable