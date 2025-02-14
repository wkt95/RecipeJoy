package com.example.recipejoy.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.recipejoy.data.model.RecipeType
import com.example.recipejoy.ui.components.RecipeCard
import com.example.recipejoy.ui.components.RecipeTypeDropdown
import com.example.recipejoy.ui.screen.recipe.list.RecipeListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel,
    onRecipeClick: (Int) -> Unit,
    onAddRecipeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.recipes.collectAsState()
    val recipeTypes by viewModel.recipeTypes.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp.dp


    val gridColumns = when {
        screenWidthDp > 840.dp -> 3
        screenWidthDp > 600.dp -> 2
        else -> 1
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RecipeJoy") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRecipeClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add New Recipe") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val horizontalPadding = when {
                screenWidthDp > 840.dp -> 32.dp
                screenWidthDp > 600.dp -> 24.dp
                else -> 16.dp
            }

            SearchAndFilterSection(
                searchText = searchText,
                onSearchTextChange = {
                    searchText = it
                    viewModel.updateSearchQuery(it)
                },
                recipeTypes = recipeTypes,
                onTypeSelected = { type ->
                    viewModel.selectRecipeType(type?.id)
                },
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteClick = { isFavorite ->
                            viewModel.toggleFavorite(recipe.id, isFavorite)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = if (isLandscape) 220.dp else 260.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    recipeTypes: List<RecipeType>,
    onTypeSelected: (RecipeType?) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                label = { Text("Search Recipes") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.weight(1f)
            )

            RecipeTypeDropdown(
                recipeTypes = recipeTypes,
                onTypeSelected = onTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                label = { Text("Search Recipes") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )

            RecipeTypeDropdown(
                recipeTypes = recipeTypes,
                onTypeSelected = onTypeSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}