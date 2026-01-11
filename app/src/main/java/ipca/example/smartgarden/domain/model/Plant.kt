package ipca.example.smartgarden.domain.model

data class Plant(
    val id: String = "",
    val userId: String = "",
    val gardenId: String = "",
    val name: String = "",
    val humidityThreshold: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)
