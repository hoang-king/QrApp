package com.example.qrgrenertor.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun QRGeneratorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6B73FF),
            secondary = Color(0xFF9B59B6),
            background = Color(0xFFF5F5F5)
        ),
        content = content
    )
}