package ipca.example.smartgarden.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlantEntity::class, GardenEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun gardenDao(): GardenDao
}
