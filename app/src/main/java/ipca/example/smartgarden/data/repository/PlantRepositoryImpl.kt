package ipca.example.smartgarden.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.smartgarden.data.local.GardenDao
import ipca.example.smartgarden.data.local.PlantDao
import ipca.example.smartgarden.data.local.toEntity
import ipca.example.smartgarden.domain.model.Garden
import ipca.example.smartgarden.domain.model.Plant
import ipca.example.smartgarden.domain.repository.PlantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val gardenDao: GardenDao,
    private val firestore: FirebaseFirestore
) : PlantRepository {

    private val plantsCollection = firestore.collection("plants")
    private val gardensCollection = firestore.collection("gardens")

    // Garden Operations
    override fun getGardens(userId: String): Flow<List<Garden>> {
        return gardenDao.getGardensByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addGarden(garden: Garden) {
        withContext(Dispatchers.IO) {
            val id = if (garden.id.isEmpty()) gardensCollection.document().id else garden.id
            val gardenWithId = garden.copy(id = id, isSynced = false)
            gardenDao.insertGarden(gardenWithId.toEntity())
            try {
                gardensCollection.document(id).set(gardenWithId.copy(isSynced = true)).await()
                gardenDao.markAsSynced(id)
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Garden saved locally", e)
            }
        }
    }

    override suspend fun updateGarden(garden: Garden) {
        withContext(Dispatchers.IO) {
            gardenDao.insertGarden(garden.copy(isSynced = false).toEntity())
            try {
                gardensCollection.document(garden.id).set(garden.copy(isSynced = true)).await()
                gardenDao.markAsSynced(garden.id)
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Garden update saved locally", e)
            }
        }
    }

    override suspend fun deleteGarden(garden: Garden) {
        withContext(Dispatchers.IO) {
            gardenDao.deleteGarden(garden.toEntity())
            try {
                gardensCollection.document(garden.id).delete().await()
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Garden delete saved locally", e)
            }
        }
    }

    override suspend fun syncGardens(userId: String) {
        withContext(Dispatchers.IO) {
            if (userId.isEmpty()) return@withContext
            try {
                val unsynced = gardenDao.getUnsyncedGardens()
                unsynced.filter { it.userId == userId }.forEach { entity ->
                    gardensCollection.document(entity.id).set(entity.toDomain().copy(isSynced = true)).await()
                    gardenDao.markAsSynced(entity.id)
                }
                val snapshot = gardensCollection.whereEqualTo("userId", userId).get().await()
                val remote = snapshot.toObjects(Garden::class.java)
                remote.forEach { gardenDao.insertGarden(it.copy(isSynced = true).toEntity()) }
            } catch (e: Exception) {
                Log.e("Firestore", "Garden sync failed", e)
            }
        }
    }

    // Plant Operations
    override fun getPlants(userId: String, gardenId: String): Flow<List<Plant>> {
        return plantDao.getPlantsByUser(userId).map { entities ->
            entities.filter { it.gardenId == gardenId }.map { it.toDomain() }
        }
    }

    override suspend fun addPlant(plant: Plant) {
        withContext(Dispatchers.IO) {
            val id = if (plant.id.isEmpty()) plantsCollection.document().id else plant.id
            val plantWithId = plant.copy(id = id, isSynced = false)
            plantDao.insertPlant(plantWithId.toEntity())
            try {
                plantsCollection.document(id).set(plantWithId.copy(isSynced = true)).await()
                plantDao.markAsSynced(id)
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Plant saved locally", e)
            }
        }
    }

    override suspend fun updatePlant(plant: Plant) {
        withContext(Dispatchers.IO) {
            plantDao.insertPlant(plant.copy(isSynced = false).toEntity())
            try {
                plantsCollection.document(plant.id).set(plant.copy(isSynced = true)).await()
                plantDao.markAsSynced(plant.id)
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Plant update saved locally", e)
            }
        }
    }

    override suspend fun deletePlant(plant: Plant) {
        withContext(Dispatchers.IO) {
            plantDao.deletePlant(plant.toEntity())
            try {
                plantsCollection.document(plant.id).delete().await()
            } catch (e: Exception) {
                Log.e("Firestore", "Offline: Plant delete saved locally", e)
            }
        }
    }

    override suspend fun getPlantById(id: String): Plant? {
        return withContext(Dispatchers.IO) {
            plantDao.getPlantById(id)?.toDomain()
        }
    }

    override suspend fun syncPlants(userId: String) {
        withContext(Dispatchers.IO) {
            if (userId.isEmpty()) return@withContext
            try {
                val unsynced = plantDao.getUnsyncedPlants()
                unsynced.filter { it.userId == userId }.forEach { entity ->
                    plantsCollection.document(entity.id).set(entity.toDomain().copy(isSynced = true)).await()
                    plantDao.markAsSynced(entity.id)
                }
                val snapshot = plantsCollection.whereEqualTo("userId", userId).get().await()
                val remotePlants = snapshot.toObjects(Plant::class.java)
                remotePlants.forEach { plant ->
                    plantDao.insertPlant(plant.copy(isSynced = true).toEntity())
                }
            } catch (e: Exception) {
                Log.e("Firestore", "Plant sync failed", e)
            }
        }
    }
}
