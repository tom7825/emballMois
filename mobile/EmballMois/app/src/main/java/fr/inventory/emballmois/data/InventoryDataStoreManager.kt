package fr.inventory.emballmois.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.inventoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "inventory_status_prefs")

class InventoryDataStoreManager(private val context: Context) {

    companion object {
        val ACTIVE_INVENTORY_MARKER_KEY = stringPreferencesKey("active_inventory_marker")
    }

    val isInventoryActiveFlow: Flow<Boolean> = context.inventoryDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ACTIVE_INVENTORY_MARKER_KEY] != null
        }

    suspend fun markInventoryAsActive() {
        context.inventoryDataStore.edit { preferences ->
            preferences[ACTIVE_INVENTORY_MARKER_KEY] = "active_inventory_session"
        }
    }

    suspend fun markInventoryAsClosed() {
        context.inventoryDataStore.edit { preferences ->
            preferences.remove(ACTIVE_INVENTORY_MARKER_KEY)
        }
    }
}