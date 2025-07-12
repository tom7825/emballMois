package fr.inventory.emballmois.ui.content.stockregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.inventory.emballmois.data.model.StockRegistrationDto
import java.math.BigDecimal
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockRegistrationModificationDialog(
    stockRegistrationToModify: StockRegistrationDto,
    onDismissRequest: () -> Unit,
    onValidate: (StockRegistrationDto) -> Unit,
    onDelete: (StockRegistrationDto) -> Unit
) {
    // État local pour gérer les modifications dans la popup
    var modifiedStockReg by remember(stockRegistrationToModify) {
        mutableStateOf(stockRegistrationToModify.copy()) // Crée une copie pour la modification
    }
    var isQuantityValid by remember(modifiedStockReg.quantity) {
        mutableStateOf(modifiedStockReg.quantity != null && modifiedStockReg.quantity!! > 0)
    }

    var quantityInput by remember { mutableStateOf(
        modifiedStockReg.quantity?.let { BigDecimal.valueOf(it).toPlainString() } ?: ""
    ) }
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Modifier la saisie pour ${modifiedStockReg.referenceName}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(15.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Fermer")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- Champs de saisie (similaire à StockRegistrationContent) ---
                Row(verticalAlignment = Alignment.Top) {
                    // TextField pour la quantité
                    Column(
                        modifier = Modifier.weight(2f),
                    ) {
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { newValue: String ->
                                quantityInput = newValue
                                val doubleValue = newValue.toDoubleOrNull()
                                if (newValue.isBlank()) {
                                    isQuantityValid = false
                                    modifiedStockReg = modifiedStockReg.copy(quantity = null)
                                } else if (doubleValue != null && doubleValue > 0 && newValue.length <= 15) {
                                    isQuantityValid = true
                                    val plainValue = BigDecimal.valueOf(doubleValue).toPlainString()

                                    modifiedStockReg =
                                        modifiedStockReg.copy(quantity = plainValue.toDouble())
                                } else {
                                    isQuantityValid = false
                                }
                            },
                            label = {
                                Text(
                                    "Quantité",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            isError = !isQuantityValid && modifiedStockReg.quantity != null
                        )
                        if (!isQuantityValid && modifiedStockReg.quantity != null) {
                            Text(
                                "Quantité > 0", // Message d'erreur simplifié
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.width(8.dp))

                    // Sélecteur d'unité
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp), // Ajout padding pour aligner
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Switch(
                            checked = modifiedStockReg.packagingCount,
                            onCheckedChange = {
                                modifiedStockReg = modifiedStockReg.copy(packagingCount = it)
                            }
                        )
                        Text(
                            if (modifiedStockReg.packagingCount) "Cond." else "Unit.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                // Icône pour commentaire
                OutlinedTextField(
                    value = modifiedStockReg.comment,
                    onValueChange = {
                        modifiedStockReg = modifiedStockReg.copy(comment = it.take(255))
                    },
                    label = { Text("Commentaire") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 150.dp)
                        .verticalScroll(rememberScrollState()),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),

                    maxLines = 4
                )
                Text(
                    "${modifiedStockReg.comment.length} / 255",
                    style = MaterialTheme.typography.bodySmall
                )
                // --- Fin Champs de saisie ---
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onDelete(modifiedStockReg) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Supprimer")
                    }
                    Button(
                        onClick = {
                            if (isQuantityValid && modifiedStockReg.quantity != null) { // S'assurer que la quantité est non nulle et valide
                                onValidate(modifiedStockReg)
                            }
                            // Optionnel: afficher un message si la quantité n'est pas valide avant de valider
                        },
                        enabled = isQuantityValid && modifiedStockReg.quantity != null // Activer seulement si valide
                    ) {
                        Text("Valider")
                    }
                }
            }
        }
    }
}