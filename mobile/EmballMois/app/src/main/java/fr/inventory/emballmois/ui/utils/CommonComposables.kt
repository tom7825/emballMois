package fr.inventory.emballmois.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.text.contains

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericExposedDropdownMenu(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemToString: (T) -> String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    isEnabled: Boolean = true,
    emptyListText: String = "Aucun élément disponible",
    defaultText: String = "Sélectionner...",
    searchHintText: String = "Rechercher..."
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(expanded, items) {
        if (!expanded) {
            searchText = ""
        }
    }

    val filteredItems = if (searchText.isBlank()) {
        items
    } else {
        items.filter { itemToString(it).contains(searchText, ignoreCase = true) }
    }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded && isEnabled,
            onExpandedChange = {
                if (isEnabled) {
                    onExpandedChange()
                    if (!expanded) {
                        searchText = ""
                    }
                }
            }
        ) {
            TextField(
                value = selectedItem?.let { itemToString(it) } ?: defaultText,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && isEnabled)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    disabledTextColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                ),
                enabled = isEnabled,
                singleLine = true,
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = isEnabled)

            )
            if (isEnabled) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        onExpandedChange()
                        searchText = ""
                    },
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                ) {
                    // Champ de recherche à l'intérieur du DropdownMenu
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        placeholder = { Text(searchHintText) },
                        singleLine = true
                    )

                    HorizontalDivider(Modifier.padding(vertical = 4.dp))

                    if (filteredItems.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(if (searchText.isNotBlank()) "Aucun résultat" else emptyListText) },
                            onClick = { },
                            enabled = false
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .height(200.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(
                                    filteredItems,
                                    key = { item -> item.toString() }
                                ) { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                itemToString(item),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        onClick = {
                                            onItemSelected(item)
                                            searchText = ""
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}