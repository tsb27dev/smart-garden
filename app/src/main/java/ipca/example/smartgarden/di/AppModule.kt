package ipca.example.smartgarden.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ipca.example.smartgarden.data.local.AppDatabase
import ipca.example.smartgarden.data.local.GardenDao
import ipca.example.smartgarden.data.local.PlantDao
import ipca.example.smartgarden.data.repository.PlantRepositoryImpl
import ipca.example.smartgarden.domain.repository.PlantRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_garden_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): PlantDao {
        return appDatabase.plantDao()
    }

    @Provides
    fun provideGardenDao(appDatabase: AppDatabase): GardenDao {
        return appDatabase.gardenDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providePlantRepository(
        plantDao: PlantDao,
        gardenDao: GardenDao,
        firestore: FirebaseFirestore
    ): PlantRepository {
        return PlantRepositoryImpl(plantDao, gardenDao, firestore)
    }
}
