package ipca.example.smartgarden.domain.repository

import ipca.example.smartgarden.domain.model.Garden
import ipca.example.smartgarden.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    // Garden operations
    fun getGardens(userId: String): Flow<List<Garden>>
    suspend fun addGarden(garden: Garden)
    suspend fun updateGarden(garden: Garden)
    suspend fun deleteGarden(garden: Garden)
    suspend fun syncGardens(userId: String)

    // Plant operations
    fun getPlants(userId: String, gardenId: String): Flow<List<Plant>>
    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    suspend fun getPlantById(id: String): Plant?
    suspend fun syncPlants(userId: String)
}
