package ipca.example.smartgarden.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.* 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ipca.example.smartgarden.R
import ipca.example.smartgarden.domain.model.Garden
import ipca.example.smartgarden.domain.model.Plant
import ipca.example.smartgarden.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: PlantViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val gardens by viewModel.gardens.collectAsState()
    val plants by viewModel.plants.collectAsState()

    var showAddEditGardenDialog by remember { mutableStateOf(false) }
    var selectedGarden by remember { mutableStateOf<Garden?>(null) }
    var showPlantsForGardenDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.1f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "Smart Garden",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Profile Settings") },
                                    onClick = { 
                                        showMenu = false
                                        onNavigateToProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = {
                                        showMenu = false
                                        authViewModel.logout(onLogout)
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedGarden = null // Clear selected garden for add operation
                        showAddEditGardenDialog = true
                    },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp) // Removed shadow
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Garden", tint = MaterialTheme.colorScheme.onPrimaryContainer)
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
                items(gardens) { garden ->
                    GardenItem(
                        garden = garden,
                        onClick = {
                            selectedGarden = garden
                            viewModel.loadPlants(garden.id) // Load plants for the selected garden
                            showPlantsForGardenDialog = true
                        },
                        onEdit = { 
                            selectedGarden = garden
                            showAddEditGardenDialog = true
                        },
                        onDelete = { viewModel.deleteGarden(garden) }
                    )
                }
            }

            if (showAddEditGardenDialog) {
                AddEditGardenDialog(
                    garden = selectedGarden,
                    onDismiss = { showAddEditGardenDialog = false },
                    onConfirm = { name ->
                        if (selectedGarden == null) {
                            viewModel.addGarden(name)
                        } else {
                            viewModel.updateGarden(selectedGarden!!.copy(name = name))
                        }
                        showAddEditGardenDialog = false
                    }
                )
            }

            if (showPlantsForGardenDialog && selectedGarden != null) {
                PlantsForGardenDialog(
                    garden = selectedGarden!!,
                    plants = plants, // Pass the plants for the current garden
                    viewModel = viewModel,
                    onDismiss = { showPlantsForGardenDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenItem(
    garden: Garden,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f) // Changed color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Removed shadow
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp) 
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = garden.name, style = MaterialTheme.typography.titleLarge)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Garden")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Garden")
                }
            }
        }
    }
}

@Composable
fun AddEditGardenDialog(
    garden: Garden?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(garden?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (garden == null) "Add Garden" else "Edit Garden") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Garden Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
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

@Composable
fun PlantsForGardenDialog(
    garden: Garden,
    plants: List<Plant>,
    viewModel: PlantViewModel,
    onDismiss: () -> Unit
) {
    var showAddEditPlantDialog by remember { mutableStateOf(false) }
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plants in ${garden.name}") },
        text = {
            Column {
                if (plants.isEmpty()) {
                    Text("No plants in this garden yet.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(plants) { plant ->
                            PlantItemInDialog(
                                plant = plant,
                                onEdit = {
                                    selectedPlant = plant
                                    showAddEditPlantDialog = true
                                },
                                onDelete = { viewModel.deletePlant(plant) }
                            )
                        }
                    }
                }

                if (showAddEditPlantDialog) {
                    AddEditPlantDialog(
                        plant = selectedPlant,
                        onDismiss = { showAddEditPlantDialog = false },
                        onConfirm = { name, humidity ->
                            if (selectedPlant == null) {
                                viewModel.addPlant(garden.id, name, humidity)
                            } else {
                                viewModel.updatePlant(selectedPlant!!.copy(name = name, humidityThreshold = humidity))
                            }
                            showAddEditPlantDialog = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            FloatingActionButton(
                onClick = {
                    selectedPlant = null
                    showAddEditPlantDialog = true
                },
                modifier = Modifier.padding(bottom = 8.dp), // Add some padding
                elevation = FloatingActionButtonDefaults.elevation(0.dp) // Removed shadow
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PlantItemInDialog(
    plant: Plant,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Removed shadow
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
                    Icon(Icons.Default.Edit, contentDescription = "Edit Plant")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Plant")
                }
            }
        }
    }
}

@Composable
fun AddEditPlantDialog(
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