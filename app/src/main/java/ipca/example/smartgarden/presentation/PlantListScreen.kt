package ipca.example.smartgarden.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ipca.example.smartgarden.domain.model.Plant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    viewModel: PlantViewModel = hiltViewModel()
) {
    val plants by viewModel.plants.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Smart Garden") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedPlant = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plants) { plant ->
                PlantItem(
                    plant = plant,
                    onEdit = {
                        selectedPlant = plant
                        showDialog = true
                    },
                    onDelete = { viewModel.deletePlant(plant) }
                )
            }
        }

        if (showDialog) {
            PlantDialog(
                plant = selectedPlant,
                onDismiss = { showDialog = false },
                onConfirm = { name, humidity ->
                    if (selectedPlant == null) {
                        viewModel.addPlant(name, humidity)
                    } else {
                        viewModel.updatePlant(selectedPlant!!.copy(name = name, humidityThreshold = humidity))
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PlantItem(
    plant: Plant,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = plant.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "Humidity: ${plant.humidityThreshold}%", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun PlantDialog(
    plant: Plant?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(plant?.name ?: "") }
    var humidity by remember { mutableStateOf(plant?.humidityThreshold?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (plant == null) "Add Plant" else "Edit Plant") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plant Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = humidity,
                    onValueChange = { if (it.all { char -> char.isDigit() }) humidity = it },
                    label = { Text("Humidity Threshold (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, humidity.toIntOrNull() ?: 0) },
                enabled = name.isNotBlank() && humidity.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
