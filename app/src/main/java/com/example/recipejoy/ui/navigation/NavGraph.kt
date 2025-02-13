package com.example.recipejoy.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipejoy.data.local.AppDatabase
import com.example.recipejoy.data.repository.RecipeRepository
import com.example.recipejoy.data.repository.RecipeTypeRepository
import com.example.recipejoy.ui.screen.AddRecipeScreen
import com.example.recipejoy.ui.screen.AddRecipeViewModel
import com.example.recipejoy.ui.screen.RecipeDetailScreen
import com.example.recipejoy.ui.screen.RecipeDetailViewModel
import com.example.recipejoy.ui.screen.EditRecipeScreen
import com.example.recipejoy.ui.screen.EditRecipeViewModel
import com.example.recipejoy.ui.screen.RecipeListScreen
import com.example.recipejoy.ui.screen.recipe.list.RecipeListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Nav.RecipeList.route
    ) {
        composable(Nav.RecipeList.route) {
            RecipeListScreen(
                viewModel = viewModel {
                    RecipeListViewModel(
                        recipeRepository = RecipeRepository(database.recipeDao()),
                        recipeTypeRepository = RecipeTypeRepository(database.recipeTypeDao())
                    )
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Nav.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(Nav.AddRecipe.route)
                }
            )
        }

        composable(Nav.AddRecipe.route) {
            AddRecipeScreen(
                viewModel = viewModel {
                    AddRecipeViewModel(
                        application = context.applicationContext as Application,
                        recipeRepository = RecipeRepository(database.recipeDao()),
                        recipeTypeRepository = RecipeTypeRepository(database.recipeTypeDao())
                    )
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Nav.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) {
            RecipeDetailScreen(
                viewModel = viewModel {
                    RecipeDetailViewModel(
                        recipeRepository = RecipeRepository(database.recipeDao()),
                        recipeTypeRepository = RecipeTypeRepository(database.recipeTypeDao()),
                        savedStateHandle = SavedStateHandle(mapOf("recipeId" to it.arguments?.getInt("recipeId")))
                    )
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
                onEditClick = { recipeId ->
                    navController.navigate(Nav.EditRecipe.createRoute(recipeId))
                }
            )
        }

        composable(
            route = Nav.EditRecipe.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) {
            EditRecipeScreen(
                viewModel = viewModel {
                    EditRecipeViewModel(
                        application = context.applicationContext as Application,
                        recipeRepository = RecipeRepository(database.recipeDao()),
                        recipeTypeRepository = RecipeTypeRepository(database.recipeTypeDao()),
                        savedStateHandle = SavedStateHandle(mapOf("recipeId" to it.arguments?.getInt("recipeId")))
                    )
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}