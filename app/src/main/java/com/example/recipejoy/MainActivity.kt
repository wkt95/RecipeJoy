package com.example.recipejoy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.recipejoy.data.local.AppDatabase
import com.example.recipejoy.ui.navigation.NavGraph
import com.example.recipejoy.ui.theme.RecipeJoyTheme
import com.example.recipejoy.utils.DatabaseLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getInstance(this)

        val dbLoader = DatabaseLoader(
            context = this,
            recipeTypeDao = database.recipeTypeDao(),
            recipeDao = database.recipeDao()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            dbLoader.preloadData()
        }

        enableEdgeToEdge()
        setContent {
            RecipeJoyTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        database = database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
