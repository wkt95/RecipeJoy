package com.example.recipejoy.data.model

enum class IngredientUnit {
    GRAMS,
    KILOGRAMS,
    MILLILITERS,
    LITERS,
    PIECES,
    TABLESPOONS,
    TEASPOONS,
    CUPS;

    fun getAbbreviation(): String {
        return when (this) {
            GRAMS -> "g"
            KILOGRAMS -> "kg"
            MILLILITERS -> "ml"
            LITERS -> "L"
            PIECES -> "pcs"
            TABLESPOONS -> "tbsp"
            TEASPOONS -> "tsp"
            CUPS -> "cup"
        }
    }
}