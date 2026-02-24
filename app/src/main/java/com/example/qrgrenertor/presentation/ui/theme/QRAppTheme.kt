package com.example.qrgrenertor.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography

// ── Brand Colors ──
object QRAppColors {
    // Primary gradient - Vibrant purple to deep blue
    val PrimaryStart = Color(0xFF7C4DFF)
    val PrimaryEnd = Color(0xFF448AFF)

    // Accent gradient - Cyan to Teal
    val AccentStart = Color(0xFF00E5FF)
    val AccentEnd = Color(0xFF1DE9B6)

    // Surface colors - Dark (glassmorphism-inspired)
    val DarkBackground = Color(0xFF0D0D1A)
    val DarkSurface = Color(0xFF1A1A2E)
    val DarkSurfaceVariant = Color(0xFF16213E)
    val DarkCard = Color(0xFF1E1E36)
    val DarkCardElevated = Color(0xFF252545)

    // Text
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB0B3C5)
    val TextTertiary = Color(0xFF6C6F85)

    // Semantic
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFFF5252)
    val Warning = Color(0xFFFFAB40)

    // Gradient brushes
    val PrimaryGradient = Brush.horizontalGradient(
        colors = listOf(PrimaryStart, PrimaryEnd)
    )
    val AccentGradient = Brush.horizontalGradient(
        colors = listOf(AccentStart, AccentEnd)
    )
    val SurfaceGradient = Brush.verticalGradient(
        colors = listOf(DarkBackground, DarkSurface)
    )
    val CardGradient = Brush.verticalGradient(
        colors = listOf(DarkCard, DarkCardElevated)
    )
    val SelectedCardGradient = Brush.linearGradient(
        colors = listOf(
            PrimaryStart.copy(alpha = 0.3f),
            PrimaryEnd.copy(alpha = 0.15f)
        )
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = QRAppColors.PrimaryStart,
    onPrimary = Color.White,
    primaryContainer = QRAppColors.PrimaryStart.copy(alpha = 0.2f),
    onPrimaryContainer = QRAppColors.PrimaryStart,
    secondary = QRAppColors.AccentStart,
    onSecondary = Color.Black,
    secondaryContainer = QRAppColors.AccentStart.copy(alpha = 0.15f),
    onSecondaryContainer = QRAppColors.AccentStart,
    tertiary = QRAppColors.AccentEnd,
    background = QRAppColors.DarkBackground,
    onBackground = QRAppColors.TextPrimary,
    surface = QRAppColors.DarkSurface,
    onSurface = QRAppColors.TextPrimary,
    surfaceVariant = QRAppColors.DarkSurfaceVariant,
    onSurfaceVariant = QRAppColors.TextSecondary,
    outline = QRAppColors.TextTertiary,
    outlineVariant = QRAppColors.DarkCardElevated,
    error = QRAppColors.Error,
    onError = Color.White
)

private val QRTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.25).sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

private val QRShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun QRAppTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = QRAppColors.DarkBackground.toArgb()
            window.navigationBarColor = QRAppColors.DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QRTypography,
        shapes = QRShapes,
        content = content
    )
}
