package fr.inventory.emballmois.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.inventory.emballmois.R
import fr.inventory.emballmois.ui.viewmodels.MainViewModel
import fr.inventory.emballmois.ui.viewmodels.SyncState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  MainScreen(
    mainViewModel: MainViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAreaChoiceScreen: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()


    LaunchedEffect(key1 = Unit) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is MainViewModel.NavigationEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Embal'Mois") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(96.dp))

            if (uiState.syncState == SyncState.SyncingBaseData) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(uiState.userMessage ?: "Chargement des données de référence...")
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        mainViewModel.startOrContinueInventory(
                            onNavigateToInventory = onNavigateToAreaChoiceScreen
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Saisir stock")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { mainViewModel.closeLocalInventory() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isInventoryActive
            ) {
                Text("Clôturer l'Inventaire (Localement)")
            }

        }
    }
}