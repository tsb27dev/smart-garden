package ipca.example.smartgarden.data.repository

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
        val newDoc = plantsCollection.document()
        val plantWithId = plant.copy(id = newDoc.id)
        
        // Save to local Room first for offline support
        plantDao.insertPlant(plantWithId.toEntity())
        
        // Save to Firestore
        newDoc.set(plantWithId).await()
    }

    override suspend fun updatePlant(plant: Plant) {
        plantDao.insertPlant(plant.toEntity())
        plantsCollection.document(plant.id).set(plant).await()
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant.toEntity())
        plantsCollection.document(plant.id).delete().await()
    }

    override suspend fun getPlantById(id: String): Plant? {
        return plantDao.getPlantById(id)?.toDomain()
    }

    override suspend fun syncPlants() {
        try {
            val snapshot = plantsCollection.get().await()
            val remotePlants = snapshot.toObjects(Plant::class.java)
            remotePlants.forEach { plant ->
                plantDao.insertPlant(plant.toEntity())
            }
        } catch (e: Exception) {
            // Handle sync error (e.g., offline)
        }
    }
}
