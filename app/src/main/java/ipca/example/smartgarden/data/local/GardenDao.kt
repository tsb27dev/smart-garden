package ipca.example.smartgarden.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {
    @Query("SELECT * FROM gardens WHERE userId = :userId ORDER BY name ASC")
    fun getGardensByUser(userId: String): Flow<List<GardenEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarden(garden: GardenEntity)

    @Delete
    suspend fun deleteGarden(garden: GardenEntity)

    @Query("SELECT * FROM gardens WHERE id = :id")
    suspend fun getGardenById(id: String): GardenEntity?

    @Query("SELECT * FROM gardens WHERE isSynced = 0")
    suspend fun getUnsyncedGardens(): List<GardenEntity>

    @Query("UPDATE gardens SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
