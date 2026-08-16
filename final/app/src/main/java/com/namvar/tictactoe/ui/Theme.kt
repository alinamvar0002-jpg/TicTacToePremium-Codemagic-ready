package com.namvar.tictactoe.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgDark = Color(0xFF0B0E1A)
val BgCard = Color(0xFF141A2E)
val AccentBlue = Color(0xFF4FC3F7)
val AccentRed = Color(0xFFFF6E6E)
val TextPrimary = Color(0xFFEDEDED)
val TextSecondary = Color(0xFF8A93A8)

private val AppColors = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentRed,
    background = BgDark,
    surface = BgCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TicTacToeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
