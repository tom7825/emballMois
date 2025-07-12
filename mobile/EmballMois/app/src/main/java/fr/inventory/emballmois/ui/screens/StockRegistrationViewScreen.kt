package fr.inventory.emballmois.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.inventory.emballmois.ui.content.stockregistration.StockRegistrationViewContent
import fr.inventory.emballmois.ui.viewmodels.AllStockRegistrationState
import fr.inventory.emballmois.ui.viewmodels.StockRegistrationViewModel

@Composable
fun StockRegistrationViewScreen(
    modifier: Modifier = Modifier,
    stockRegistrationViewModel: StockRegistrationViewModel,
    onReturnHome: () -> Unit
) {
    val allStockEntries by stockRegistrationViewModel.allStockRegistrations.collectAsStateWithLifecycle()
    val currentLoadingState =
        stockRegistrationViewModel.allStockRegistrationsState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        stockRegistrationViewModel.loadAllStockRegistration()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
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
            Spacer(modifier = Modifier.padding(vertical = 16.dp))
            if (currentLoadingState.value is AllStockRegistrationState.Loading) {
                Text("Chargement en cours...")
                CircularProgressIndicator()
            } else if (currentLoadingState.value is AllStockRegistrationState.Error) {
                Text("Erreur : ${(currentLoadingState.value as AllStockRegistrationState.Error).message}")
            } else {
                if (allStockEntries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucune saisie trouvée.")
                    }
                } else {
                    LazyColumn {
                        items(
                            items = allStockEntries,
                            key = { it.id }
                        ) { entry ->
                            StockRegistrationViewContent(
                                stockRegistration = entry,
                                stockRegistrationViewModel = stockRegistrationViewModel
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
