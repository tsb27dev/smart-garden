package ipca.example.smartgarden.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    var isResetPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.1f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when {
                    isResetPassword -> "Reset Password"
                    isSignUp -> "Create Account"
                    else -> "Welcome Back"
                },
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (!isResetPassword) {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
                    if (email.isBlank()) {
                        errorMessage = "Email cannot be empty"
                        return@Button
                    }
                    if (isResetPassword) {
                        viewModel.resetPassword(email, 
                            onSuccess = { successMessage = "Check your email to reset password" },
                            onError = { errorMessage = it }
                        )
                    } else if (isSignUp) {
                        if (password.isBlank()) { errorMessage = "Password required"; return@Button }
                        viewModel.signUp(email, password, onLoginSuccess) { errorMessage = it }
                    } else {
                        if (password.isBlank()) { errorMessage = "Password required"; return@Button }
                        viewModel.login(email, password, onLoginSuccess) { errorMessage = it }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(when {
                    isResetPassword -> "Send Reset Link"
                    isSignUp -> "Sign Up"
                    else -> "Login"
                })
            }

            if (!isResetPassword && !isSignUp) {
                TextButton(onClick = { isResetPassword = true }) {
                    Text("Forgot Password?")
                }
            }

            TextButton(onClick = { 
                isSignUp = !isSignUp 
                isResetPassword = false
                errorMessage = null
                successMessage = null
            }) {
                Text(if (isSignUp) "Already have an account? Login" else "Don't have an account? Sign Up")
            }
            
            if (isResetPassword) {
                TextButton(onClick = { isResetPassword = false }) {
                    Text("Back to Login")
                }
            }
        }
    }
}
