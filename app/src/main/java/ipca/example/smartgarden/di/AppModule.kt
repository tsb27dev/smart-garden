package ipca.example.smartgarden.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ipca.example.smartgarden.data.local.AppDatabase
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
        ).build()
    }

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): PlantDao {
        return appDatabase.plantDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providePlantRepository(
        plantDao: PlantDao,
        firestore: FirebaseFirestore
    ): PlantRepository {
        return PlantRepositoryImpl(plantDao, firestore)
    }
}
