package ipca.example.smartgarden.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.smartgarden.domain.model.Garden
import ipca.example.smartgarden.domain.model.Plant
import ipca.example.smartgarden.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _gardens = MutableStateFlow<List<Garden>>(emptyList())
    val gardens: StateFlow<List<Garden>> = _gardens.asStateFlow()

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    init {
        viewModelScope.launch {
            repository.getGardens(userId).collect {
                _gardens.value = it
            }
        }
        
        viewModelScope.launch {
            if (userId.isNotEmpty()) {
                repository.syncGardens(userId)
                repository.syncPlants(userId)
            }
        }
    }

    fun addGarden(name: String) {
        viewModelScope.launch {
            repository.addGarden(Garden(userId = userId, name = name))
        }
    }

    fun updateGarden(garden: Garden) {
        viewModelScope.launch {
            repository.updateGarden(garden.copy(userId = userId))
        }
    }

    fun deleteGarden(garden: Garden) {
        viewModelScope.launch {
            repository.deleteGarden(garden)
        }
    }

    fun loadPlants(gardenId: String) {
        viewModelScope.launch {
            repository.getPlants(userId, gardenId).collect {
                _plants.value = it
            }
        }
    }

    fun addPlant(gardenId: String, name: String, humidity: Int) {
        viewModelScope.launch {
            repository.addPlant(Plant(userId = userId, gardenId = gardenId, name = name, humidityThreshold = humidity))
        }
    }

    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            repository.updatePlant(plant.copy(userId = userId))
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }
}
