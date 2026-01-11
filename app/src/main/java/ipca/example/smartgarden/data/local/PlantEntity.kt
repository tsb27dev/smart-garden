package ipca.example.smartgarden.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ipca.example.smartgarden.domain.model.Plant

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val gardenId: String,
    val name: String,
    val humidityThreshold: Int,
    val lastUpdated: Long,
    val isSynced: Boolean = true
) {
    fun toDomain() = Plant(id, userId, gardenId, name, humidityThreshold, lastUpdated, isSynced)
}

fun Plant.toEntity() = PlantEntity(id, userId, gardenId, name, humidityThreshold, lastUpdated, isSynced)
