package ipca.example.smartgarden.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlantEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
