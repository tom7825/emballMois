package fr.inventory.emballmois.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.PackagingReferenceStorageAreaCrossRef
import fr.inventory.emballmois.data.model.StockRegistration
import fr.inventory.emballmois.data.model.StorageArea

@Database(
    entities = [
        StorageArea::class,
        PackagingReference::class,
        PackagingReferenceStorageAreaCrossRef::class,
        StockRegistration::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storageAreaDao(): StorageAreaDao
    abstract fun packagingReferenceDao(): PackagingReferenceDao
    abstract fun packagingReferenceStorageAreaCrossRefDao(): PackagingReferenceStorageAreaCrossRefDao
    abstract fun stockRegistrationDao(): StockRegistrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database"
                ).fallbackToDestructiveMigration(true) //TODO pas en prod
                    // .addMigrations(MIGRATION_1_2, ...)
                   .build()
                INSTANCE = instance
                instance
            }
        }
    }
}