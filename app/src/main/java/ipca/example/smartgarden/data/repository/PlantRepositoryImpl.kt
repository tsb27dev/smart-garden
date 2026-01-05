package ipca.example.smartgarden.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.smartgarden.data.local.PlantDao
import ipca.example.smartgarden.data.local.toEntity
import ipca.example.smartgarden.domain.model.Plant
import ipca.example.smartgarden.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val firestore: FirebaseFirestore
) : PlantRepository {

    private val plantsCollection = firestore.collection("plants")

    override fun getPlants(): Flow<List<Plant>> {
        return plantDao.getAllPlants().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addPlant(plant: Plant) {
        val id = if (plant.id.isEmpty()) plantsCollection.document().id else plant.id
        val plantWithId = plant.copy(id = id, isSynced = false)
        
        // Save to Room first (isSynced = false)
        plantDao.insertPlant(plantWithId.toEntity())
        
        try {
            plantsCollection.document(id).set(plantWithId.copy(isSynced = true)).await()
            plantDao.markAsSynced(id)
            Log.d("Firestore", "Successfully added plant: ${plant.name}")
        } catch (e: Exception) {
            Log.e("Firestore", "Offline: Plant saved locally, will sync later", e)
        }
    }

    override suspend fun updatePlant(plant: Plant) {
        val updatedPlant = plant.copy(isSynced = false)
        plantDao.insertPlant(updatedPlant.toEntity())
        try {
            plantsCollection.document(plant.id).set(plant.copy(isSynced = true)).await()
            plantDao.markAsSynced(plant.id)
            Log.d("Firestore", "Successfully updated plant: ${plant.name}")
        } catch (e: Exception) {
            Log.e("Firestore", "Offline: Update saved locally", e)
        }
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant.toEntity())
        try {
            plantsCollection.document(plant.id).delete().await()
            Log.d("Firestore", "Successfully deleted plant from Firestore")
        } catch (e: Exception) {
            Log.e("Firestore", "Offline: Deleted locally, might persist in cloud until next sync", e)
        }
    }

    override suspend fun getPlantById(id: String): Plant? {
        return plantDao.getPlantById(id)?.toDomain()
    }

    override suspend fun syncPlants() {
        // 1. Upload unsynced local changes to Firestore
        try {
            val unsynced = plantDao.getUnsyncedPlants()
            unsynced.forEach { entity ->
                val plant = entity.toDomain()
                plantsCollection.document(plant.id).set(plant.copy(isSynced = true)).await()
                plantDao.markAsSynced(plant.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Sync upload failed", e)
        }

        // 2. Download changes from Firestore
        try {
            val snapshot = plantsCollection.get().await()
            val remotePlants = snapshot.toObjects(Plant::class.java)
            remotePlants.forEach { plant ->
                plantDao.insertPlant(plant.copy(isSynced = true).toEntity())
            }
            Log.d("Firestore", "Sync download completed")
        } catch (e: Exception) {
            Log.e("Firestore", "Sync download failed", e)
        }
    }
}
