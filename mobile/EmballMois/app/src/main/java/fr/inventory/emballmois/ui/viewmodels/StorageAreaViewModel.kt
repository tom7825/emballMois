package fr.inventory.emballmois.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import android.util.Log // Pour les logs
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.data.repository.StorageAreaRepository
import javax.inject.Inject

sealed class StorageAreaUiState {
    object Loading : StorageAreaUiState()
    data class Success(val storageAreas: List<StorageArea>) : StorageAreaUiState()
    data class Error(val message: String) : StorageAreaUiState()
}


@HiltViewModel
class StorageAreaViewModel @Inject constructor(
    private val storageAreaRepository: StorageAreaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<StorageAreaUiState>(StorageAreaUiState.Loading)
    val uiState: StateFlow<StorageAreaUiState> = _uiState.asStateFlow()

    init {
        observeStorageAreasFromDb()
    }

    private fun observeStorageAreasFromDb() {
        viewModelScope.launch {
            storageAreaRepository.allStorageAreas
                .catch { exception ->
                    Log.e("StorageAreaViewModel", "Erreur lors de l'observation des zones de la DB", exception)
                    _uiState.value = StorageAreaUiState.Error("Erreur base de données: ${exception.message}")
                }
                .collect { areas ->
                    if (areas.isNotEmpty()) {
                        _uiState.value = StorageAreaUiState.Success(areas)
                        Log.d("StorageAreaViewModel", "Zones observées de la DB: ${areas.size}")
                    } else if (_uiState.value !is StorageAreaUiState.Error) {
                        _uiState.value = StorageAreaUiState.Success(areas)
                        Log.d("StorageAreaViewModel", "DB vide, en attente de données réseau ou pas de données.")
                    }
                }
        }
    }
}

