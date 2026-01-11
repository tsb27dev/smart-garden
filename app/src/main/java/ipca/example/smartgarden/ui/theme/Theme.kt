package ipca.example.smartgarden.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    primaryContainer = DarkGreen.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = Green80,
    onSecondary = Color.Black,
    secondaryContainer = Green80.copy(alpha = 0.2f),
    onSecondaryContainer = Color.Black,
    tertiary = LightGreen,
    onTertiary = Color.White,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE0E0E0),
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green40.copy(alpha = 0.2f),
    onPrimaryContainer = Color.Black,
    secondary = LightGreen,
    onSecondary = Color.Black,
    secondaryContainer = LightGreen.copy(alpha = 0.2f),
    onSecondaryContainer = Color.Black,
    tertiary = DarkGreen,
    onTertiary = Color.White,
    background = Green80, // Changed to a light green
    onBackground = Color(0xFF1C1B1F),
    surface = Green80, // Changed to a light green
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun SmartgardenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Changed to false to force custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}