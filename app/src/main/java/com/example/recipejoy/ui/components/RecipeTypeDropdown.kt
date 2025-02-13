package com.example.recipejoy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipejoy.data.model.RecipeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeTypeDropdown(
    recipeTypes: List<RecipeType>,
    onTypeSelected: (RecipeType?) -> Unit,
    modifier: Modifier = Modifier,
    selectedType: RecipeType? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedType?.name ?: "All Recipe Types",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp
                    else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Recipe Types") },
                onClick = {
                    onTypeSelected(null)
                    expanded = false
                }
            )

            recipeTypes.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(type.name)
                    },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeTypeDropdownPreview() {
    val sampleTypes = listOf(
        RecipeType(1, "Main Course", "Main dishes"),
        RecipeType(2, "Dessert", "Sweet treats"),
        RecipeType(3, "Appetizer", "Starters")
    )

    MaterialTheme {
        RecipeTypeDropdown(
            recipeTypes = sampleTypes,
            onTypeSelected = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}