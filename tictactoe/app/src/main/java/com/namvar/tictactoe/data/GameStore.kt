package com.namvar.tictactoe.data

import android.content.Context
import android.content.SharedPreferences

data class Stats(
    val games: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0
)

data class Settings(
    val soundOn: Boolean = true,
    val musicOn: Boolean = true,
    val vibrationOn: Boolean = true,
    val darkTheme: Boolean = true
)

class GameStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tictactoe_prefs", Context.MODE_PRIVATE)

    fun loadStats(): Stats = Stats(
        games = prefs.getInt("games", 0),
        wins = prefs.getInt("wins", 0),
        losses = prefs.getInt("losses", 0),
        draws = prefs.getInt("draws", 0),
        bestStreak = prefs.getInt("bestStreak", 0),
        currentStreak = prefs.getInt("currentStreak", 0)
    )

    fun recordWin() {
        val s = loadStats()
        val newStreak = s.currentStreak + 1
        prefs.edit()
            .putInt("games", s.games + 1)
            .putInt("wins", s.wins + 1)
            .putInt("currentStreak", newStreak)
            .putInt("bestStreak", maxOf(s.bestStreak, newStreak))
            .apply()
    }

    fun recordLoss() {
        val s = loadStats()
        prefs.edit()
            .putInt("games", s.games + 1)
            .putInt("losses", s.losses + 1)
            .putInt("currentStreak", 0)
            .apply()
    }

    fun recordDraw() {
        val s = loadStats()
        prefs.edit()
            .putInt("games", s.games + 1)
            .putInt("draws", s.draws + 1)
            .putInt("currentStreak", 0)
            .apply()
    }

    fun resetStats() {
        prefs.edit()
            .putInt("games", 0).putInt("wins", 0).putInt("losses", 0)
            .putInt("draws", 0).putInt("bestStreak", 0).putInt("currentStreak", 0)
            .apply()
    }

    fun loadSettings(): Settings = Settings(
        soundOn = prefs.getBoolean("soundOn", true),
        musicOn = prefs.getBoolean("musicOn", true),
        vibrationOn = prefs.getBoolean("vibrationOn", true),
        darkTheme = prefs.getBoolean("darkTheme", true)
    )

    fun saveSettings(settings: Settings) {
        prefs.edit()
            .putBoolean("soundOn", settings.soundOn)
            .putBoolean("musicOn", settings.musicOn)
            .putBoolean("vibrationOn", settings.vibrationOn)
            .putBoolean("darkTheme", settings.darkTheme)
            .apply()
    }
}
