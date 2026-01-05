package ipca.example.smartgarden.domain.repository

import ipca.example.smartgarden.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getPlants(): Flow<List<Plant>>
    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    suspend fun getPlantById(id: String): Plant?
    suspend fun syncPlants()
}
