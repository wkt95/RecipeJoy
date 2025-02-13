package com.example.recipejoy.ui.navigation

sealed class Nav(val route: String) {
    data object RecipeList : Nav("recipeList")
    data object AddRecipe : Nav("addRecipe")
    data object RecipeDetail : Nav("recipeDetail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipeDetail/$recipeId"
    }
    data object EditRecipe : Nav("editRecipe/{recipeId}") {
        fun createRoute(recipeId: Int) = "editRecipe/$recipeId"
    }
}
