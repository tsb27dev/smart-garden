package ipca.example.smartgarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.smartgarden.presentation.PlantListScreen
import ipca.example.smartgarden.presentation.auth.AuthViewModel
import ipca.example.smartgarden.presentation.auth.LoginScreen
import ipca.example.smartgarden.presentation.auth.ProfileScreen
import ipca.example.smartgarden.ui.theme.SmartgardenTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartgardenTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val user by authViewModel.user.collectAsState()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = if (user == null) "login" else "plants"
                ) {
                    composable("login") {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("plants") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    }
                    composable("plants") {
                        PlantListScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("plants") { inclusive = true }
                                }
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onBack = { navController.popBackStack() },
                            onAccountDeleted = {
                                navController.navigate("login") {
                                    popUpTo("plants") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
