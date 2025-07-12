package fr.inventory.emballmois.di

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.inventory.emballmois.data.dao.*
import fr.inventory.emballmois.data.InventoryDataStoreManager
import fr.inventory.emballmois.data.network.AuthApiService
import fr.inventory.emballmois.data.network.AuthInterceptor
import fr.inventory.emballmois.data.network.InventoryApiService
import fr.inventory.emballmois.data.network.PackagingReferenceApiService
import fr.inventory.emballmois.data.network.StockRegistrationApiService
import fr.inventory.emballmois.data.network.StorageAreaApiService
import fr.inventory.emballmois.data.repository.UserPreferencesRepository // Important pour AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.java
import fr.inventory.emballmois.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Fourniture de la Base de Données et des DAOs  ---
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideStorageAreaDao(appDatabase: AppDatabase): StorageAreaDao {
        return appDatabase.storageAreaDao()
    }

    @Provides
    fun providePackagingReferenceDao(appDatabase: AppDatabase): PackagingReferenceDao {
        return appDatabase.packagingReferenceDao()
    }

    @Provides
    fun provideStockRegistrationDao(appDatabase: AppDatabase): StockRegistrationDao {
        return appDatabase.stockRegistrationDao()
    }

    @Provides
    fun providePackagingReferenceStorageAreaCrossRefDao(appDatabase: AppDatabase): PackagingReferenceStorageAreaCrossRefDao {
        return appDatabase.packagingReferenceStorageAreaCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideInventoryDataStoreManager(@ApplicationContext context: Context): InventoryDataStoreManager {
        return InventoryDataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (true) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferencesRepository: UserPreferencesRepository): AuthInterceptor {
        return AuthInterceptor(userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    // --- Fourniture des Services API ---
    @Provides
    @Singleton
    fun provideStorageAreaApiService(retrofit: Retrofit): StorageAreaApiService {
        return retrofit.create(StorageAreaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePackagingReferenceApiService(retrofit: Retrofit): PackagingReferenceApiService {
        return retrofit.create(PackagingReferenceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryApiService(retrofit: Retrofit): InventoryApiService {
        return retrofit.create(InventoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStockRegistrationApiService(retrofit: Retrofit): StockRegistrationApiService {
        return retrofit.create(StockRegistrationApiService::class.java)
    }
}