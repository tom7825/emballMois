package fr.inventory.emballmois.ui.content.stockregistration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.inventory.emballmois.data.model.StockRegistration

@Composable
fun StockRegistrationContent(
    stockRegistration: StockRegistration,
    onDataChange: (StockRegistration) -> Unit
) {
    var isQuantityValid by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var quantityInput by remember { mutableStateOf(stockRegistration.quantity?.toString() ?: "") }

    LaunchedEffect(stockRegistration.quantity) {
        isQuantityValid = stockRegistration.quantity != null && stockRegistration.quantity.toString().isNotEmpty() && stockRegistration.quantity!! > 0
    }

    Row {
        // TextField pour la quantité
        Column (
            modifier = Modifier.weight(2f),
        ) {
            OutlinedTextField(
                value = quantityInput,
                onValueChange = { newValue  ->
                    quantityInput = newValue
                    val doubleValue = newValue.toDoubleOrNull()
                    if (newValue.isBlank()) {
                        isQuantityValid = false
                        onDataChange(stockRegistration.copy(quantity = null))
                    } else if (doubleValue != null && doubleValue > 0 && newValue.length <= 15) {
                        isQuantityValid = true
                        onDataChange(stockRegistration.copy(quantity = doubleValue))
                    } else {
                        isQuantityValid = false
                    }
                },
                label = { Text("Quantité", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = stockRegistration.quantity != null && stockRegistration.quantity.toString().isNotEmpty() && !isQuantityValid
            )
            if (stockRegistration.quantity != null && stockRegistration.quantity.toString().isNotEmpty() && !isQuantityValid) {
                Text(
                    "Veuillez entrer une quantité valide",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        // Sélecteur d'unité (Conditionné / Unitaire)
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Switch(
                checked = stockRegistration.packagingCountState,
                onCheckedChange = { onDataChange(stockRegistration.copy(packagingCountState = it))}
            )
            Text(if (stockRegistration.packagingCountState) "Conditionné" else "Unitaire", style = MaterialTheme.typography.bodySmall)
        }

        // Icône pour ouvrir/modifier le commentaire
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = { showCommentDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Ouvrir/Modifier le commentaire"
                )
            }
            Text("Commentaire", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }

    if (showCommentDialog) {
        CommentDialog(
            initialComment = stockRegistration.commentText,
            onDismiss = { showCommentDialog = false },
            onSave = { newComment ->
                onDataChange(stockRegistration.copy(commentText = newComment))
                showCommentDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDialog(
    initialComment: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var currentComment by remember(initialComment) { mutableStateOf(initialComment) }

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
                Text("Commentaire", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = currentComment,
                    onValueChange = { currentComment = it.take(255) },
                    label = { Text("Saisissez votre commentaire") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 5
                )
                Text("${currentComment.length} / 255", style = MaterialTheme.typography.bodySmall)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(currentComment) }) {
                        Text("Sauvegarder")
                    }
                }
            }
        }
    }
}