package fr.inventory.emballmois.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.inventory.emballmois.data.repository.InventoryRepository
import fr.inventory.emballmois.data.repository.PackagingReferenceRepository
import fr.inventory.emballmois.data.repository.StorageAreaRepository
import fr.inventory.emballmois.ui.utils.MessageManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface SyncState {
    object Idle : SyncState
    object SyncingBaseData : SyncState
    object SyncingToBackend : SyncState
}

data class MainScreenUiState(
    val syncState: SyncState = SyncState.Idle,
    val isInventoryActive: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storageAreaRepository: StorageAreaRepository,
    private val packagingReferenceRepository: PackagingReferenceRepository,
    private val inventoryRepository: InventoryRepository,
    private val messageManager: MessageManager
) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    private val _userMessage = MutableStateFlow<String?>(null) // Pour les messages qui ne sont pas gérés par messageManager

    private val _navigationEvents = Channel<NavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    private val isInventoryActiveFlow: StateFlow<Boolean> =
        inventoryRepository.isInventoryActiveFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val uiState: StateFlow<MainScreenUiState> = combine(
        isInventoryActiveFlow,
        _syncState.asStateFlow(),
        _userMessage.asStateFlow()
    ) { isActive, currentSyncState, currentUserMessage ->
        MainScreenUiState(
            isInventoryActive = isActive,
            syncState = currentSyncState,
            userMessage = currentUserMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainScreenUiState() // Valeurs par défaut de la data class
    )

    suspend fun synchronizeBaseData(): Boolean {
        _syncState.value = SyncState.SyncingBaseData
        _userMessage.value = "Synchronisation des données de référence en cours..." // Ou via messageManager

        return try {
            storageAreaRepository.refreshStorageAreas()
            val listOfAreas = storageAreaRepository.allStorageAreas.first()

            if (listOfAreas.isEmpty()) {
                _userMessage.value = "Aucune zone de stockage trouvée. Impossible de continuer."
                _syncState.value = SyncState.Idle // Ou SyncState.Error si vous l'introduisez
                false
            } else {
                packagingReferenceRepository.clearAll()
                for (area in listOfAreas) {
                    packagingReferenceRepository.loadReferenceForArea(area.name)
                }
                _userMessage.value = "Données de référence synchronisées avec succès."
                _syncState.value = SyncState.Idle
                true
            }
        } catch (e: Exception) {
            handleSyncError(e, "Erreur de synchronisation des données de référence")
            false
        } finally {
            if (_syncState.value == SyncState.SyncingBaseData) {
                _syncState.value = SyncState.Idle
            }

        }
    }

    fun startOrContinueInventory(onNavigateToInventory: () -> Unit) {
        viewModelScope.launch {
            try {
                if (isInventoryActiveFlow.value) {
                    messageManager.showMessage("Reprise de l'inventaire en cours...")
                    onNavigateToInventory()
                } else {
                    if (synchronizeBaseData()) {
                        inventoryRepository.startInventory()
                        messageManager.showMessage("Nouvel inventaire démarré.")
                        onNavigateToInventory()
                    } else {
                        messageManager.showMessage("Impossible de démarrer un nouvel inventaire suite à une erreur de synchronisation.")
                    }
                }
            } catch (e: Exception) {
                _userMessage.value = "Erreur lors du démarrage de l'inventaire: ${e.message}"
                Log.e("MainViewModel", "Error starting inventory", e) // Loggez l'exception
            }
        }
    }

    fun closeLocalInventory() {
        viewModelScope.launch {
            if (!isInventoryActiveFlow.value) {
                _userMessage.value = "Aucun inventaire actif à clôturer."
                return@launch
            }
            _syncState.value = SyncState.SyncingToBackend
            _userMessage.value = "Clôture de l'inventaire local en cours..."
            try {
                inventoryRepository.synchDataAndCloseLocalInventory()
                packagingReferenceRepository.clearAll()
                storageAreaRepository.clearAll()

                messageManager.showMessage("Inventaire clôturé localement. Données enregistrées sur le serveur.")
            } catch (e: Exception) {
                handleSyncError(e, "Erreur lors de la clôture de l'inventaire local")
            } finally {
                _syncState.value = SyncState.Idle
            }
        }
    }

    private suspend fun handleSyncError(e: Exception, defaultMessagePrefix: String) {
        _syncState.value = SyncState.Idle // Ou SyncState.Error si défini
        if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
            messageManager.showMessage("Session expirée ou accès refusé. Veuillez vous reconnecter.")
            _navigationEvents.send(NavigationEvent.NavigateToLogin)
        } else {
            messageManager.showMessage("$defaultMessagePrefix: ${e.message ?: "Erreur inconnue"}")
        }
        Log.e("MainViewModel", "$defaultMessagePrefix exception", e) // Toujours logger l'exception
    }

    sealed class NavigationEvent {
        object NavigateToLogin : NavigationEvent()
    }
}