package ipca.example.smartgarden.domain.model

data class Garden(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)
