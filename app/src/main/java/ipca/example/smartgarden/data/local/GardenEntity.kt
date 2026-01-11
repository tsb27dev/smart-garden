package ipca.example.smartgarden.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ipca.example.smartgarden.domain.model.Garden

@Entity(tableName = "gardens")
data class GardenEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val lastUpdated: Long,
    val isSynced: Boolean = true
) {
    fun toDomain() = Garden(id, userId, name, lastUpdated, isSynced)
}

fun Garden.toEntity() = GardenEntity(id, userId, name, lastUpdated, isSynced)
