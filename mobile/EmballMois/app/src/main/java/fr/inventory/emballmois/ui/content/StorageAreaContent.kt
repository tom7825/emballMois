package fr.inventory.emballmois.ui.content

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.ui.theme.EmballMoisTheme
import fr.inventory.emballmois.ui.viewmodels.StorageAreaUiState
import fr.inventory.emballmois.ui.viewmodels.StorageAreaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StorageAreaContent(
    storageAreaViewModel: StorageAreaViewModel = hiltViewModel(),
    onAreaSelected: (StorageArea) -> Unit
) {
    val uiState by storageAreaViewModel.uiState.collectAsState()

    Text(
        "Selection zone de Stockage",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    when (val state = uiState) {
        is StorageAreaUiState.Loading -> {
            CircularProgressIndicator()
            Text("Chargement des zones...")
        }
        is StorageAreaUiState.Success -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.storageAreas.forEach { area ->
                    ElevatedButton(
                        onClick = { onAreaSelected(area) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .width(300.dp)
                            .height(60.dp)
                    ) {
                        Text(
                            text = area.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        is StorageAreaUiState.Error -> {
            Text("Erreur: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 360)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAreaScreenPreview_Dropdown_Success() {
    val fakeAreas = listOf(
        StorageArea(1, "Zone A (Entrepôt Principal)"),
        StorageArea(2, "Zone B (Réserve Magasin)"),
        StorageArea(3, "Zone C (Atelier)")
    )
    var selectedPreviewArea by remember { mutableStateOf<StorageArea?>(fakeAreas.firstOrNull()) }

    EmballMoisTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Sélectionner Zone (Preview)") })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                selectedPreviewArea?.let {
                    Text("Sélectionné : ${it.name}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
fun StorageAreaScreenPreview_Dropdown_Loading() {
    EmballMoisTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Sélectionner Zone (Preview)") })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Chargement des zones...")
            }
        }
    }
}