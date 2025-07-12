package fr.inventory.emballmois.ui.content

import android.annotation.SuppressLint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.ui.utils.GenericExposedDropdownMenu
import fr.inventory.emballmois.ui.viewmodels.ReferenceUiState
import fr.inventory.emballmois.ui.viewmodels.ReferenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReferenceContent(
    modifier: Modifier = Modifier,
    referenceViewModel: ReferenceViewModel = hiltViewModel(),
    currentSelectedReference: PackagingReference?,
    onReferenceSelected: (PackagingReference) -> Unit,
    selectedArea: StorageArea?
) {
    val uiState by referenceViewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedArea) {
        referenceViewModel.loadReferencesForArea(selectedArea)
    }


    when (val state = uiState) {
        is ReferenceUiState.Loading -> {
            CircularProgressIndicator()
            Text("Chargement des références...")
        }

        is ReferenceUiState.Success -> {
            GenericExposedDropdownMenu(
                label = "Référence",
                items = state.packagingReferences,
                selectedItem = currentSelectedReference,
                onItemSelected = { reference ->
                    onReferenceSelected(reference)
                    expanded = false
                },
                itemToString = { reference -> reference.name },
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                isEnabled = selectedArea != null && state.packagingReferences.isNotEmpty(),
                emptyListText = if (selectedArea == null) "Sélectionner d'abord une zone" else "Aucune référence pour cette zone",
                defaultText = "Sélectionner une référence",
            )
        }

        is ReferenceUiState.Error -> {
            Text("Erreur: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
    }
}