package com.namvar.tictactoe.game

enum class Mark { EMPTY, X, O }

enum class Difficulty { EASY, MEDIUM, HARD, EXPERT }

enum class GameResult { NONE, X_WIN, O_WIN, DRAW }

private val WIN_LINES = listOf(
    intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
    intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
    intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
)

fun checkResult(board: Array<Mark>): Pair<GameResult, IntArray?> {
    for (line in WIN_LINES) {
        val (a, b, c) = Triple(board[line[0]], board[line[1]], board[line[2]])
        if (a != Mark.EMPTY && a == b && b == c) {
            return Pair(if (a == Mark.X) GameResult.X_WIN else GameResult.O_WIN, line)
        }
    }
    if (board.none { it == Mark.EMPTY }) return Pair(GameResult.DRAW, null)
    return Pair(GameResult.NONE, null)
}

fun emptyCells(board: Array<Mark>): List<Int> =
    board.indices.filter { board[it] == Mark.EMPTY }

/**
 * Picks the AI's move. aiMark is the mark the AI plays as.
 */
fun aiMove(board: Array<Mark>, aiMark: Mark, difficulty: Difficulty): Int {
    val opponent = if (aiMark == Mark.X) Mark.O else Mark.X
    val empty = emptyCells(board)
    if (empty.isEmpty()) return -1

    return when (difficulty) {
        Difficulty.EASY -> empty.random()

        Difficulty.MEDIUM -> {
            // 50% best move, 50% random
            if (Math.random() < 0.5) bestMove(board, aiMark, opponent) else empty.random()
        }

        Difficulty.HARD -> {
            // 80% best move, 20% random
            if (Math.random() < 0.8) bestMove(board, aiMark, opponent) else empty.random()
        }

        Difficulty.EXPERT -> bestMove(board, aiMark, opponent)
    }
}

private fun bestMove(board: Array<Mark>, aiMark: Mark, opponent: Mark): Int {
    var bestScore = Int.MIN_VALUE
    var move = -1
    for (i in emptyCells(board)) {
        val copy = board.copyOf()
        copy[i] = aiMark
        val score = minimax(copy, 0, false, aiMark, opponent)
        if (score > bestScore) {
            bestScore = score
            move = i
        }
    }
    return move
}

private fun minimax(board: Array<Mark>, depth: Int, isMaximizing: Boolean, aiMark: Mark, opponent: Mark): Int {
    val (result, _) = checkResult(board)
    when (result) {
        GameResult.DRAW -> return 0
        GameResult.X_WIN -> return if (aiMark == Mark.X) 10 - depth else depth - 10
        GameResult.O_WIN -> return if (aiMark == Mark.O) 10 - depth else depth - 10
        GameResult.NONE -> {}
    }

    return if (isMaximizing) {
        var best = Int.MIN_VALUE
        for (i in emptyCells(board)) {
            val copy = board.copyOf()
            copy[i] = aiMark
            best = maxOf(best, minimax(copy, depth + 1, false, aiMark, opponent))
        }
        best
    } else {
        var best = Int.MAX_VALUE
        for (i in emptyCells(board)) {
            val copy = board.copyOf()
            copy[i] = opponent
            best = minOf(best, minimax(copy, depth + 1, true, aiMark, opponent))
        }
        best
    }
}
