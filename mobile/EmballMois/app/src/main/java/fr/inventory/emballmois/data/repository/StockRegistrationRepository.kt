package fr.inventory.emballmois.data.repository

import android.util.Log
import fr.inventory.emballmois.data.dao.StockRegistrationDao
import fr.inventory.emballmois.data.model.ApiResponse
import fr.inventory.emballmois.data.model.StockRegistration
import fr.inventory.emballmois.data.model.StockRegistrationDto
import fr.inventory.emballmois.data.network.StockRegistrationApiService
import retrofit2.HttpException
import javax.inject.Inject

class StockRegistrationRepository @Inject constructor(
    private val stockRegistrationDao: StockRegistrationDao,
    private val stockRegistrationApiService: StockRegistrationApiService
) {
    suspend fun registerStock(stockRegistration: StockRegistration) {
        stockRegistrationDao.insert(stockRegistration)
    }

    suspend fun getAllStockRegistrations() : List<StockRegistrationDto> {
        return stockRegistrationDao.getAllStockRegistration()
    }

    suspend fun updateStockRegistration(dto: StockRegistrationDto) {

        val result = stockRegistrationDao.updateStockRegistration(
            id = dto.id,
            quantity = dto.quantity,
            packagingCountState = dto.packagingCount,
            commentText = dto.comment
        )
        if(result == 0){
            throw Exception("Aucun enregistrement modifié")
        }

    }

    suspend fun deleteStockRegistration(id: Int) {
        val result = stockRegistrationDao.deleteStockRegistration(id)
        if(result == 0){
            throw Exception("Aucun enregistrement modifié")
        }
    }

    suspend fun saveStockRegistrations() {
        val stockRegistrations = stockRegistrationDao.getAllStockRegistration()
        for (stockRegistration in stockRegistrations) {
            try {
                Log.d("StockRegistrationRepository", "Enregistrement à sauvegarder : $stockRegistration")
                stockRegistrationApiService.saveStockRegistrations(stockRegistration)
                stockRegistrationDao.deleteStockRegistration(stockRegistration.id)
            }catch(e: HttpException){
                Log.e("StockRegistrationRepository", "Erreur lors de la sauvegarde des enregistrements", e)
                throw e
                return
            }
        }


    }

}