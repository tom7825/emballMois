package fr.inventory.emballmois.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.ui.content.StorageAreaContent


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaSelectionScreen(onAreaSelected: (StorageArea) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 20.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        StorageAreaContent(
            onAreaSelected = { area ->
                onAreaSelected(area)
            }
        )
    }
}
