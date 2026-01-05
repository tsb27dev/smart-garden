package ipca.example.smartgarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.smartgarden.presentation.PlantListScreen
import ipca.example.smartgarden.ui.theme.SmartgardenTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartgardenTheme {
                PlantListScreen()
            }
        }
    }
}
