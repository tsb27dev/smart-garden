package ipca.example.smartgarden.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ipca.example.smartgarden.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var confirmPasswordForDelete by remember { mutableStateOf("") }
    var confirmPasswordForDeleteVisible by remember { mutableStateOf(false) }

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
                    title = { Text("Profile Settings") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "User: ${viewModel.user.collectAsState().value?.email}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                TextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = if (currentPasswordVisible) "Hide password" else "Show password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = if (newPasswordVisible) "Hide password" else "Show password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
                successMessage?.let {
                    Text(text = it, color = Color.Green, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        if (currentPassword.isBlank()) {
                            errorMessage = "Current password is required"
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            errorMessage = "New password should be at least 6 characters"
                            return@Button
                        }
                        viewModel.updatePassword(currentPassword, newPassword, 
                            onSuccess = { 
                                successMessage = "Password updated successfully!"
                                currentPassword = ""
                                newPassword = ""
                            },
                            onError = { errorMessage = it }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Password")
                }

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account", color = Color.White)
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    confirmPasswordForDelete = ""
                    confirmPasswordForDeleteVisible = false
                },
                title = { Text("Delete Account") },
                text = {
                    Column {
                        Text("Are you sure you want to delete your account? This action cannot be undone. Please enter your password to confirm.")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = confirmPasswordForDelete,
                            onValueChange = { confirmPasswordForDelete = it },
                            label = { Text("Password") },
                            visualTransformation = if (confirmPasswordForDeleteVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (confirmPasswordForDeleteVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { confirmPasswordForDeleteVisible = !confirmPasswordForDeleteVisible }) {
                                    Icon(imageVector = image, contentDescription = if (confirmPasswordForDeleteVisible) "Hide password" else "Show password")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (confirmPasswordForDelete.isBlank()) return@Button
                            viewModel.deleteAccount(
                                password = confirmPasswordForDelete,
                                onSuccess = onAccountDeleted,
                                onError = { 
                                    errorMessage = it
                                    showDeleteDialog = false
                                    confirmPasswordForDelete = ""
                                    confirmPasswordForDeleteVisible = false
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeleteDialog = false
                        confirmPasswordForDelete = ""
                        confirmPasswordForDeleteVisible = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
