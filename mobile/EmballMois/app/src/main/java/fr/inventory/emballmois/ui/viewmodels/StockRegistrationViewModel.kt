package fr.inventory.emballmois.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.StockRegistration
import fr.inventory.emballmois.data.model.StockRegistrationDto
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.data.repository.StockRegistrationRepository
import fr.inventory.emballmois.ui.utils.MessageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StockRegistrationSaveState {
    object Idle : StockRegistrationSaveState()
    object Loading : StockRegistrationSaveState()
    object Success : StockRegistrationSaveState()
    data class Error(val message: String) : StockRegistrationSaveState()
}

sealed class AllStockRegistrationState {
    object Idle : AllStockRegistrationState()
    object Loading : AllStockRegistrationState()
    object Success : AllStockRegistrationState()
    data class Error(val message: String) : AllStockRegistrationState()
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Success : UpdateState()
    object Error : UpdateState()
}

@HiltViewModel
class StockRegistrationViewModel @Inject constructor(
    val stockRegistrationRepository: StockRegistrationRepository,
    val messageManager: MessageManager
) : ViewModel() {

    private val _stockRegistrations = MutableStateFlow<List<StockRegistration>>(emptyList())
    private val _saveState =
        MutableStateFlow<StockRegistrationSaveState>(StockRegistrationSaveState.Idle)
    private val _allStockRegistrationsState =
        MutableStateFlow<AllStockRegistrationState>(AllStockRegistrationState.Idle)
    private val _selectedStorageArea = MutableStateFlow<StorageArea?>(null)
    private val _selectedReference = MutableStateFlow<PackagingReference?>(null)
    private val _allStockRegistrations = MutableStateFlow<List<StockRegistrationDto>>(emptyList())
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)

    val saveState: StateFlow<StockRegistrationSaveState> = _saveState.asStateFlow()
    val allStockRegistrationsState: StateFlow<AllStockRegistrationState> =
        _allStockRegistrationsState.asStateFlow()
    val stockRegistrations: StateFlow<List<StockRegistration>> = _stockRegistrations.asStateFlow()
    val selectedStorageArea: StateFlow<StorageArea?> = _selectedStorageArea.asStateFlow()
    val selectedReference: StateFlow<PackagingReference?> = _selectedReference.asStateFlow()
    val allStockRegistrations: StateFlow<List<StockRegistrationDto>> =
        _allStockRegistrations.asStateFlow()


    init {
        initializeStockRegistrations()
    }

    fun initializeData() {
        initializeStockRegistrations()
        _selectedReference.value = null
        _saveState.value = StockRegistrationSaveState.Idle
        _allStockRegistrationsState.value = AllStockRegistrationState.Idle
    }


    fun initializeStockRegistrations(count: Int = 4) {
        _stockRegistrations.update {
            List(count) { StockRegistration(id = System.identityHashCode(Any())) }
        }
    }

    fun updateSelectedReference(reference: PackagingReference?) {
        _selectedReference.value = reference
    }

    fun updateStockRegistrationItem(index: Int, updatedItem: StockRegistration) {
        _stockRegistrations.update { currentList ->
            currentList.toMutableList().also { newList ->
                if (index >= 0 && index < newList.size) {
                    newList[index] = updatedItem
                }
            }
        }
    }

    fun saveAllStockRegistrations() {
        viewModelScope.launch {
            _saveState.value = StockRegistrationSaveState.Loading
            if (_selectedReference.value == null || _selectedStorageArea.value == null) {
                Log.e("StockRegViewModel", "Référence ou Zone non sélectionnée pour la sauvegarde.")
                return@launch
            }

            _stockRegistrations.value.forEach { item ->
                val quantity = item.quantity
                if (quantity != null && quantity > 0) {
                    val itemToSave = item.copy(
                        packagingReferenceId = _selectedReference.value?.id,
                        storageAreaId = _selectedStorageArea.value?.id
                    )
                    if (itemToSave.packagingReferenceId == null) {
                        _saveState.value = StockRegistrationSaveState.Error("Référence manquante.")
                        return@forEach
                    }
                    if (itemToSave.storageAreaId == null) {
                        _saveState.value = StockRegistrationSaveState.Error("Zone manquante.")
                        return@forEach
                    }
                    saveStockRegistration(itemToSave)

                }
            }
            _saveState.value = StockRegistrationSaveState.Success
            initializeStockRegistrations()
            _selectedReference.value = null
            Log.d("StockRegViewModel", "Enregistrement groupé terminé.")
        }
    }

    suspend fun saveStockRegistration(stockRegistration: StockRegistration) {
        stockRegistrationRepository.registerStock(stockRegistration)
        Log.d("StockRegistrationViewModel", "Enregistrement du stock: $stockRegistration")
    }

    fun saveStateInitialized() {
        _saveState.value = StockRegistrationSaveState.Idle
    }

    fun selectStorageArea(area: StorageArea?) {
        _selectedStorageArea.value = area
        initializeData()
    }

    suspend fun loadAllStockRegistration() {
        _allStockRegistrationsState.value = AllStockRegistrationState.Loading
        _allStockRegistrations.value = stockRegistrationRepository.getAllStockRegistrations()
        if (_allStockRegistrations.value.isEmpty()) {
            _allStockRegistrationsState.value =
                AllStockRegistrationState.Error("Aucun enregistrement trouvé")
        } else {
            _allStockRegistrationsState.value = AllStockRegistrationState.Success
        }
    }

    fun updateStockRegistration(dto: StockRegistrationDto) {
        viewModelScope.launch {
            try {
                stockRegistrationRepository.updateStockRegistration(dto)
                _updateState.value = UpdateState.Success
                messageManager.showMessage("Mise à jour effectuée avec succès")
                loadAllStockRegistration()
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error
                messageManager.showMessage(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun deleteStockRegistration(dto: StockRegistrationDto) {
        viewModelScope.launch {
            try {
                stockRegistrationRepository.deleteStockRegistration(id = dto.id)
                _updateState.value = UpdateState.Success
                messageManager.showMessage("Suppression effectuée avec succès")
                loadAllStockRegistration()
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error
                messageManager.showMessage(e.message ?: "Erreur inconnue")
            }
        }
    }
}

