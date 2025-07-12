package fr.inventory.emballmois

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import fr.inventory.emballmois.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EmbalMoisApplication : Application() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    private val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            try {
                userPreferencesRepository.clearAuthToken()
            } catch (e: Exception) {
                Log.e("EmballMoisApplication", "Error clearing auth token", e)
            }
        }

    }
}