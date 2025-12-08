package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// --- UI Colors ---
val PrimaryGreen = Color(0xFF4CAF50)
val PrimaryGreenDark = Color(0xFF388E3C)
val LightGreen = Color(0xFFE8F5E9)
val DarkGreen = Color(0xFF1B5E20)
val WarningYellow = Color(0xFFFFF9C4)
val WarningYellowDark = Color(0xFFF9A825)
val AlertRed = Color(0xFFFFEBEE)
val AlertRedDark = Color(0xFFD32F2F)
val TextDark = Color(0xFF1B1B1F)
val TextLight = Color(0xFFE3E3E3)
val TextGray = Color(0xFF5E5E62)
val TextGrayLight = Color(0xFFB0B0B0)
val SurfaceDark = Color(0xFF1E1E1E)
val BackgroundDark = Color(0xFF121212)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = Color(0xFF8BC34A),
    tertiary = Color(0xFFCDDC39),
    background = Color(0xFFF5F7F8),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = Color(0xFF8BC34A),
    tertiary = Color(0xFFCDDC39),
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight
)

// App shapes
private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        content = content
    )
}

/**
 * Get background color for food item based on days until expiry
 */
@Composable
fun getFoodItemColors(daysUntilExpiry: Long, isDarkTheme: Boolean = isSystemInDarkTheme()): Triple<Color, Color, String> {
    return when {
        daysUntilExpiry < 0 -> Triple(
            if (isDarkTheme) AlertRedDark.copy(alpha = 0.3f) else AlertRed,
            if (isDarkTheme) Color(0xFFFF6B6B) else Color.Red,
            "Expired"
        )
        daysUntilExpiry <= 3 -> Triple(
            if (isDarkTheme) WarningYellowDark.copy(alpha = 0.3f) else WarningYellow,
            if (isDarkTheme) Color(0xFFFFD93D) else Color(0xFFF57F17),
            "$daysUntilExpiry days left"
        )
        else -> Triple(
            if (isDarkTheme) SurfaceDark else Color.White,
            if (isDarkTheme) Color(0xFF81C784) else PrimaryGreen,
            "$daysUntilExpiry days left"
        )
    }
}