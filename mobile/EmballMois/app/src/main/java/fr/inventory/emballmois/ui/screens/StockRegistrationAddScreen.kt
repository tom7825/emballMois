package fr.inventory.emballmois.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.inventory.emballmois.ui.content.ReferenceContent
import fr.inventory.emballmois.ui.content.stockregistration.StockRegistrationContent
import fr.inventory.emballmois.ui.utils.findSimilarStrings
import fr.inventory.emballmois.ui.viewmodels.ReferenceViewModel
import fr.inventory.emballmois.ui.viewmodels.StockRegistrationSaveState
import fr.inventory.emballmois.ui.viewmodels.StockRegistrationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockRegistrationAddScreen(
    modifier: Modifier = Modifier,
    stockRegistrationViewModel: StockRegistrationViewModel,
    onViewStockRegistrationSelected: () -> Unit,
    onReturnHome: () -> Unit,
    referenceViewModel: ReferenceViewModel = hiltViewModel(),
) {

    var showNewReferenceDialog by remember { mutableStateOf(false) }
    var newReferenceNameState by remember { mutableStateOf("") }
    var similarStrings by remember { mutableStateOf(emptySet<String>()) }

    val selectedReference by stockRegistrationViewModel.selectedReference.collectAsStateWithLifecycle()
    val selectedArea by stockRegistrationViewModel.selectedStorageArea.collectAsStateWithLifecycle()
    val stockRegistrations by stockRegistrationViewModel.stockRegistrations.collectAsStateWithLifecycle()
    val saveState by stockRegistrationViewModel.saveState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()



    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Enregistrement du Stock",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        onReturnHome()
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Retour à l'accueil",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReferenceContent(
                    currentSelectedReference = selectedReference,
                    onReferenceSelected = { reference ->
                        stockRegistrationViewModel.updateSelectedReference(reference)
                    },
                    selectedArea = selectedArea,
                    modifier = Modifier.width(260.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        newReferenceNameState = ""
                        showNewReferenceDialog = true
                    },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)

                ) {
                    Text("+", textAlign = TextAlign.Center)
                }
                Spacer(Modifier.weight(1f))
            }
            selectedReference?.let {
                if(it.unitByPackaging != 0){
                    Text("Unités par conditionnement : ${selectedReference!!.unitByPackaging}")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (selectedReference != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        stockRegistrations,
                        key = { _, item -> item.id }) { index, itemData ->
                        StockRegistrationContent(
                            stockRegistration = itemData,
                            onDataChange = { updatedStockRegistration ->
                                stockRegistrationViewModel.updateStockRegistrationItem(
                                    index,
                                    updatedStockRegistration
                                )
                            }
                        )
                        if (index < stockRegistrations.lastIndex) {
                            HorizontalDivider(
                                Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 16.dp
                                )
                            )
                        }
                    }
                }
                if (saveState is StockRegistrationSaveState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            stockRegistrationViewModel.saveAllStockRegistrations()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .align(Alignment.CenterHorizontally),

                        ) {
                        Text("Enregistrer Tout")
                    }
                }
            }
        }

        //Button d'affichage des saisies de stock
        FloatingActionButton(
            onClick = {
                stockRegistrationViewModel.saveStateInitialized()
                onViewStockRegistrationSelected()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Visualiser toutes les saisies de stock"
            )
        }
    }

    //Gestion des états
    LaunchedEffect(saveState) {
        when (val currentState = saveState) {
            is StockRegistrationSaveState.Success -> {
                Toast.makeText(
                    context,
                    "Enregistrement réussi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is StockRegistrationSaveState.Error -> {
                Toast.makeText(
                    context,
                    "Erreur durant l'enregistrement : ${currentState.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    AddReferenceDialog(
        showNewReferenceDialog = showNewReferenceDialog,
        currentName = newReferenceNameState,
        similarReferences = similarStrings,
        onNameChange = {
            newReferenceNameState = it
            if(it.length > 2) {
                scope.launch {
                    val allReference = referenceViewModel.allReferences.value
                    similarStrings =
                        findSimilarStrings(it, allReference.map { reference -> reference.name }, 3)
                    Log.d("SimilarityMatch", "Similar strings: $similarStrings")
                }
            }
        },
        onDismiss = { showNewReferenceDialog = false },
        onSave = { name ->
            scope.launch {
                if (referenceViewModel.addReference(name, selectedArea?.name ?: "")) {
                    referenceViewModel.loadReferencesForArea(selectedArea)
                    showNewReferenceDialog = false
                }
            }
        },
        onSimilarReferenceClick = { clickedReferenceName ->
            newReferenceNameState = clickedReferenceName
            similarStrings = emptySet()
        },
        warningText = "Attention : le nom de la référence ne pourra pas être modifié à postériori !"
    )
}

@Composable
fun AddReferenceDialog(
    showNewReferenceDialog: Boolean,
    currentName: String,
    similarReferences: Set<String>,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onSimilarReferenceClick: (String) -> Unit,
    warningText: String
) {
    if (!showNewReferenceDialog) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Nouvelle Référence",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = currentName,
                    onValueChange = onNameChange,
                    label = { Text("Nom de la référence") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (similarReferences.isNotEmpty() && currentName.isNotBlank()) {
                    Text(
                        "Références similaires :",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 100.dp)) {
                        items(similarReferences.toList()) { refName ->
                            Text(
                                text = refName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSimilarReferenceClick(refName) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            HorizontalDivider()
                        }
                    }
                }


                Text(
                    text = warningText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (currentName.isNotBlank()) {
                                onSave(currentName)
                            }
                        },
                        enabled = currentName.isNotBlank() // Désactiver si le nom est vide
                    ) {
                        Text("Valider")
                    }
                }
            }
        }
    }
}
