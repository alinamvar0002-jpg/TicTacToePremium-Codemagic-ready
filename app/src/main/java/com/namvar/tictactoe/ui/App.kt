package com.namvar.tictactoe.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvar.tictactoe.data.GameStore
import com.namvar.tictactoe.data.Settings
import com.namvar.tictactoe.game.*

@Composable
fun TicTacToeApp() {
    val context = LocalContext.current
    val store = remember { GameStore(context) }

    var screen by remember { mutableStateOf<Screen>(Screen.MainMenu) }
    var mode by remember { mutableStateOf(GameMode.PVAI) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var settings by remember { mutableStateOf(store.loadSettings()) }

    fun updateSettings(newSettings: Settings) {
        settings = newSettings
        store.saveSettings(newSettings)
    }

    TicTacToeTheme {
        Surface(color = BgDark) {
            AnimatedContent(targetState = screen, label = "nav") { s ->
                when (s) {
                    is Screen.MainMenu -> MainMenuScreen(
                        onPlay = { mode = GameMode.PVP; screen = Screen.Game },
                        onPlayAI = { mode = GameMode.PVAI; screen = Screen.DifficultySelect },
                        onSettings = { screen = Screen.Settings },
                        onStats = { screen = Screen.Statistics },
                        onAbout = { screen = Screen.About }
                    )
                    is Screen.ModeSelect -> Unit
                    is Screen.DifficultySelect -> DifficultySelectScreen(
                        onSelect = { d -> difficulty = d; screen = Screen.Game },
                        onBack = { screen = Screen.MainMenu }
                    )
                    is Screen.Game -> GameScreen(
                        mode = mode,
                        difficulty = difficulty,
                        store = store,
                        settings = settings,
                        onExit = { screen = Screen.MainMenu }
                    )
                    is Screen.Settings -> SettingsScreen(
                        settings = settings,
                        onChange = ::updateSettings,
                        onResetStats = { store.resetStats() },
                        onBack = { screen = Screen.MainMenu }
                    )
                    is Screen.Statistics -> StatisticsScreen(
                        store = store,
                        onBack = { screen = Screen.MainMenu }
                    )
                    is Screen.About -> AboutScreen(onBack = { screen = Screen.MainMenu })
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onPlayAI: () -> Unit,
    onSettings: () -> Unit,
    onStats: () -> Unit,
    onAbout: () -> Unit
) {
    GradientBackground {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⭕✖️", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Tic Tac Toe",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                "PREMIUM",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(40.dp))
            PremiumButton("Play (2 Players)", onClick = onPlay)
            Spacer(Modifier.height(12.dp))
            PremiumButton("Play vs AI", onClick = onPlayAI)
            Spacer(Modifier.height(12.dp))
            SecondaryButton("Settings", onClick = onSettings)
            Spacer(Modifier.height(12.dp))
            SecondaryButton("Statistics", onClick = onStats)
            Spacer(Modifier.height(12.dp))
            SecondaryButton("About", onClick = onAbout)
        }
    }
}

@Composable
fun DifficultySelectScreen(onSelect: (Difficulty) -> Unit, onBack: () -> Unit) {
    GradientBackground {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ScreenTitle("Select Difficulty")
            PremiumButton("Easy", onClick = { onSelect(Difficulty.EASY) })
            Spacer(Modifier.height(12.dp))
            PremiumButton("Medium", onClick = { onSelect(Difficulty.MEDIUM) })
            Spacer(Modifier.height(12.dp))
            PremiumButton("Hard", onClick = { onSelect(Difficulty.HARD) })
            Spacer(Modifier.height(12.dp))
            PremiumButton("Expert", onClick = { onSelect(Difficulty.EXPERT) })
            Spacer(Modifier.height(24.dp))
            SecondaryButton("Back", onClick = onBack)
        }
    }
}

@Composable
fun GameScreen(
    mode: GameMode,
    difficulty: Difficulty,
    store: GameStore,
    settings: Settings,
    onExit: () -> Unit
) {
    var board by remember { mutableStateOf(Array(9) { Mark.EMPTY }) }
    var currentPlayer by remember { mutableStateOf(Mark.X) }
    var scoreX by remember { mutableStateOf(0) }
    var scoreO by remember { mutableStateOf(0) }
    var result by remember { mutableStateOf(GameResult.NONE) }
    var winLine by remember { mutableStateOf<IntArray?>(null) }
    var recorded by remember { mutableStateOf(false) }

    fun resetBoard() {
        board = Array(9) { Mark.EMPTY }
        currentPlayer = Mark.X
        result = GameResult.NONE
        winLine = null
        recorded = false
    }

    fun applyResult(r: GameResult) {
        if (recorded) return
        recorded = true
        when (r) {
            GameResult.X_WIN -> {
                scoreX++
                if (mode == GameMode.PVAI) store.recordWin() else Unit
            }
            GameResult.O_WIN -> {
                scoreO++
                if (mode == GameMode.PVAI) store.recordLoss() else Unit
            }
            GameResult.DRAW -> {
                if (mode == GameMode.PVAI) store.recordDraw() else Unit
            }
            else -> {}
        }
    }

    fun place(index: Int) {
        if (board[index] != Mark.EMPTY || result != GameResult.NONE) return
        val newBoard = board.copyOf()
        newBoard[index] = currentPlayer
        board = newBoard
        val (r, line) = checkResult(newBoard)
        if (r != GameResult.NONE) {
            result = r
            winLine = line
            applyResult(r)
        } else {
            currentPlayer = if (currentPlayer == Mark.X) Mark.O else Mark.X
        }
    }

    // AI move (AI always plays O)
    LaunchedEffect(board, currentPlayer, result, mode) {
        if (mode == GameMode.PVAI && currentPlayer == Mark.O && result == GameResult.NONE) {
            kotlinx.coroutines.delay(400)
            val move = aiMove(board, Mark.O, difficulty)
            if (move >= 0) place(move)
        }
    }

    GradientBackground {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreBadge("X", scoreX, AccentBlue)
                Text(
                    if (mode == GameMode.PVAI) "vs AI (${difficulty.name})" else "2 Players",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                ScoreBadge("O", scoreO, AccentRed)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = when {
                    result == GameResult.X_WIN -> "X Wins! 🎉"
                    result == GameResult.O_WIN -> if (mode == GameMode.PVAI) "AI Wins" else "O Wins! 🎉"
                    result == GameResult.DRAW -> "Draw"
                    else -> "Turn: ${if (currentPlayer == Mark.X) "X" else "O"}"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            BoardView(board = board, winLine = winLine, onCellClick = { place(it) })

            Spacer(Modifier.height(28.dp))

            if (result != GameResult.NONE) {
                PremiumButton("Play Again", onClick = { resetBoard() })
                Spacer(Modifier.height(12.dp))
            } else {
                PremiumButton("Restart", onClick = { resetBoard() })
                Spacer(Modifier.height(12.dp))
            }
            SecondaryButton("Main Menu", onClick = onExit)
        }
    }
}

@Composable
fun ScoreBadge(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(score.toString(), color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BoardView(board: Array<Mark>, winLine: IntArray?, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(10.dp)
    ) {
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val idx = row * 3 + col
                    CellView(
                        mark = board[idx],
                        highlighted = winLine?.contains(idx) == true,
                        onClick = { onCellClick(idx) }
                    )
                }
            }
        }
    }
}

@Composable
fun CellView(mark: Mark, highlighted: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(
        targetValue = if (highlighted) AccentBlue.copy(alpha = 0.25f) else Color(0xFF1B2238),
        label = "cellBg"
    )
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(88.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(enabled = mark == Mark.EMPTY) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = mark != Mark.EMPTY, enter = scaleIn() + fadeIn()) {
            Text(
                text = when (mark) {
                    Mark.X -> "X"
                    Mark.O -> "O"
                    Mark.EMPTY -> ""
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = if (mark == Mark.X) AccentBlue else AccentRed
            )
        }
    }
}

@Composable
fun SettingsScreen(
    settings: Settings,
    onChange: (Settings) -> Unit,
    onResetStats: () -> Unit,
    onBack: () -> Unit
) {
    GradientBackground {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            ScreenTitle("Settings")
            SettingRow("Sound", settings.soundOn) { onChange(settings.copy(soundOn = it)) }
            SettingRow("Music", settings.musicOn) { onChange(settings.copy(musicOn = it)) }
            SettingRow("Vibration", settings.vibrationOn) { onChange(settings.copy(vibrationOn = it)) }
            SettingRow("Dark Theme", settings.darkTheme) { onChange(settings.copy(darkTheme = it)) }
            Spacer(Modifier.height(20.dp))
            SecondaryButton("Reset Statistics", onClick = onResetStats)
            Spacer(Modifier.height(12.dp))
            PremiumButton("Back", onClick = onBack)
        }
    }
}

@Composable
fun SettingRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPrimary, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue)
        )
    }
}

@Composable
fun StatisticsScreen(store: GameStore, onBack: () -> Unit) {
    val stats = remember { store.loadStats() }
    GradientBackground {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            ScreenTitle("Statistics")
            StatRow("Games Played", stats.games)
            StatRow("Wins", stats.wins)
            StatRow("Losses", stats.losses)
            StatRow("Draws", stats.draws)
            StatRow("Best Streak", stats.bestStreak)
            Spacer(Modifier.height(24.dp))
            PremiumButton("Back", onClick = onBack)
        }
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 15.sp)
        Text(value.toString(), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    GradientBackground {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ScreenTitle("About")
            Text(
                "Tic Tac Toe Premium v1.0.0\nBuilt with Kotlin + Jetpack Compose.\nFeatures a Minimax-powered AI opponent.",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            PremiumButton("Back", onClick = onBack)
        }
    }
}
