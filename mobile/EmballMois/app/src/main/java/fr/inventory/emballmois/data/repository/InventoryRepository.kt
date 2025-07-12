package fr.inventory.emballmois.data.repository

import android.util.Log
import fr.inventory.emballmois.data.InventoryDataStoreManager
import fr.inventory.emballmois.data.network.InventoryApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val inventoryDataStoreManager: InventoryDataStoreManager,
    private val inventoryApiService: InventoryApiService,
    private val stockRegistrationRepository: StockRegistrationRepository,
    private val referenceRepository: PackagingReferenceRepository
) {
    val isInventoryActiveFlow: Flow<Boolean> = inventoryDataStoreManager.isInventoryActiveFlow

    suspend fun startInventory() {
        inventoryDataStoreManager.markInventoryAsActive()
        Log.d("InventoryRepository", "Inventaire marqué comme DÉMARRÉ localement.")
    }

    suspend fun synchDataAndCloseLocalInventory() {
        if(isInventoryRunning()){
            referenceRepository.saveNewReferences()
            stockRegistrationRepository.saveStockRegistrations()
        }else{
            inventoryApiService.createInventory()
            referenceRepository.saveNewReferences()
            stockRegistrationRepository.saveStockRegistrations()
        }

        inventoryDataStoreManager.markInventoryAsClosed()
        Log.d("InventoryRepository", "Inventaire marqué comme CLÔTURÉ localement.")
    }

    private suspend fun isInventoryRunning(): Boolean {
        try {
            inventoryApiService.isBackInventoryRunning()
            return true
        } catch (e: HttpException) {
            if (e.code() == 404) {
                return false
            } else if (e.code() == 200){

            }else{
                throw e
            }
            return false
        }
    }
}