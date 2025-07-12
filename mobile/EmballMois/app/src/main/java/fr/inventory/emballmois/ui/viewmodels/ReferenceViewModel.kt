package fr.inventory.emballmois.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.data.repository.PackagingReferenceRepository
import fr.inventory.emballmois.data.repository.PackagingReferenceStorageAreaCrossRefRepository
import fr.inventory.emballmois.ui.utils.MessageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReferenceUiState {
    data object Loading : ReferenceUiState
    data class Success(val packagingReferences: List<PackagingReference>) : ReferenceUiState
    data class Error(val message: String) : ReferenceUiState
}

@HiltViewModel
class ReferenceViewModel @Inject constructor(
    val packagingReferenceStorageAreaCrossRefRepository: PackagingReferenceStorageAreaCrossRefRepository,
    val packagingReferenceRepository: PackagingReferenceRepository,
    val messageManager: MessageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReferenceUiState>(ReferenceUiState.Loading)
    val uiState: StateFlow<ReferenceUiState> = _uiState

    private val _allReferences = MutableStateFlow<List<PackagingReference>>(emptyList())
    val allReferences: StateFlow<List<PackagingReference>> = _allReferences


    fun loadReferencesForArea(storageArea: StorageArea?) {
        if (storageArea == null) {
            _uiState.value = ReferenceUiState.Error("Zone de stockage non sélectionnée")
            return
        }

        viewModelScope.launch {
            _uiState.value = ReferenceUiState.Loading
            try {
                val references =
                    packagingReferenceStorageAreaCrossRefRepository.getStorageAreaWithPackagingReferences(
                        storageArea.id
                    )
                _allReferences.value = packagingReferenceRepository.getAllPackagingReferences()
                if (references != null) {
                    if (references.packagingReferences.isEmpty()) {
                        _uiState.value =
                            ReferenceUiState.Error("Aucune référence trouvée pour cette zone")
                        return@launch
                    }
                    _uiState.value = ReferenceUiState.Success(references.packagingReferences)
                }
            } catch (e: Exception) {
                _uiState.value =
                    ReferenceUiState.Error("Erreur chargement références: ${e.message}")
            }
        }
    }

    suspend fun addReference(name: String, areaName: String): Boolean {
        if (name.isBlank()) {
            return false
        }
        return try {
            val reference = packagingReferenceRepository.getReferenceByName(name)
            if (reference == null) {
                packagingReferenceStorageAreaCrossRefRepository.addReference(
                    PackagingReference(
                        name = name,
                        isNew = true
                    ),
                    areaName
                )
                Log.d("ReferenceViewModel", "Référence ajoutée avec succès : $name")
                messageManager.showMessage("Référence '$name' ajoutée avec succès.")
                true
            } else {
                packagingReferenceStorageAreaCrossRefRepository.insertPackagingReferenceStorageAreaCrossRef(
                    listOf(reference),
                    areaName
                )
                Log.d("ReferenceViewModel", "Référence déjà existante : $name, insertion de la zone de stockage")
                messageManager.showMessage("Ajout de la référence '$name' à la zone de stockage")
                true
            }
        } catch (e: Exception) {
            Log.e(
                "ReferenceViewModel",
                "Erreur lors de l'ajout de la référence '$name': ${e.message}",
                e
            )
            messageManager.showMessage("Erreur lors de l'ajout de la référence '$name': ${e.message}")
            false
        }
    }
}