package com.virex.app.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VirexColors = darkColorScheme(
    primary = Color(0xFF7C4DFF),
    secondary = Color(0xFF00E5FF),
    tertiary = Color(0xFFFF2D55),
    background = Color(0xFF050505),
    surface = Color(0xFF101322),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun VirexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VirexColors,
        content = content
    )
}
