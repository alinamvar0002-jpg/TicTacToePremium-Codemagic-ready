package com.namvar.tictactoe.ui

sealed class Screen {
    object MainMenu : Screen()
    object ModeSelect : Screen()
    object DifficultySelect : Screen()
    object Game : Screen()
    object Settings : Screen()
    object Statistics : Screen()
    object About : Screen()
}

enum class GameMode { PVP, PVAI }
