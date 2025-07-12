package fr.inventory.emballmois.ui.content.stockregistration

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.inventory.emballmois.data.model.StockRegistrationDto
import fr.inventory.emballmois.ui.viewmodels.StockRegistrationViewModel

@Composable
fun StockRegistrationViewContent(
    modifier: Modifier = Modifier,
    stockRegistration: StockRegistrationDto,
    stockRegistrationViewModel: StockRegistrationViewModel
) {
    Log.d("ViewContent", "Recomposition pour ID: ${stockRegistration.id} - Data: $stockRegistration")
    var showModificationDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isExpanded) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = stockRegistration.referenceName,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.weight(1F))
                IconButton(
                    onClick = { showModificationDialog = true },
                    modifier = modifier.size(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Modifier"
                    )
                }
            }
            Spacer(Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Qté: ${String.format("%.2f", stockRegistration.quantity ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Zone: ${stockRegistration.storageAreaName ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Cdmt.: ${if (stockRegistration.packagingCount) "Conditionné" else "Unitaire"}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier.padding(vertical = 4.dp))
            Text(
                text = "Commentaire: ${if (stockRegistration.comment.isNotEmpty()) stockRegistration.comment else "-"}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stockRegistration.referenceName,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(2f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Qté: ${String.format("%.2f", stockRegistration.quantity ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(2f)
                )

                Text(
                    text = if (stockRegistration.packagingCount) "Cond." else "Unité",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.weight(1F))
                IconButton(
                    onClick = { showModificationDialog = true },
                    modifier = Modifier.size(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Modifier"
                    )
                }
            }
        }
    }
    if (showModificationDialog) {
        StockRegistrationModificationDialog(
            stockRegistrationToModify = stockRegistration,
            onDismissRequest = { showModificationDialog = false },
            onValidate = { updatedStockReg ->
                stockRegistrationViewModel.updateStockRegistration(updatedStockReg)
                showModificationDialog = false
            },
            onDelete = { stockRegToDelete ->
                stockRegistrationViewModel.deleteStockRegistration(stockRegToDelete)
                showModificationDialog = false
            }
        )
    }
}