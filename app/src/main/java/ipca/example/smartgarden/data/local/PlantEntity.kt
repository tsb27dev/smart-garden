package ipca.example.smartgarden.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ipca.example.smartgarden.domain.model.Plant

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val humidityThreshold: Int,
    val lastUpdated: Long
) {
    fun toDomain() = Plant(id, name, humidityThreshold, lastUpdated)
}

fun Plant.toEntity() = PlantEntity(id, name, humidityThreshold, lastUpdated)
