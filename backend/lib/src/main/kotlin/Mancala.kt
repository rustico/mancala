package lib

/**
 * Main game logic of Mancala.
 *
 * We have a board of twelve pits and two banks (bit pits "[0]") in each side where the stones are collected.
 * Each pit has 6 stones in it and the banks starts without any stone.
 *
 *        Player one
 *  [0]  6, 6, 6, 6, 6, 6,
 *       6, 6, 6, 6, 6, 6  [0]
 *        Player two
 *
 * This board is represented in a mutable list of integers: [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]
 *
 * The representation of the list indexes in the board would be:
 *
 *        Player one
 *  [6]  5, 4, 3, 2,   1, 0,
 *       7, 8, 9, 10, 11, 12  [13]
 *        Player two
 */
class MancalaGame (
    var playerTurn: MancalaPlayer = MancalaPlayer.PlayerOne,
    val numberOfStones : Int = 6
) {
    // We divide the main board in two.
    // Each banks contains the total stones that a player has.
    private val playerOneBankIndex = 6
    var playerOneBank: Int
        get() = board[playerOneBankIndex]
        set(value) {
            board[playerOneBankIndex] = value
        }

    private val playerTwoBankIndex = 13
    var playerTwoBank: Int
        get() = board[playerTwoBankIndex]
        set(value) {
            board[playerTwoBankIndex] = value
        }

    val board = MutableList(14) { if (it == playerOneBankIndex || it == playerTwoBankIndex) 0 else numberOfStones }
    val playerOneBoard : MutableList<Int>
        get() = board.subList(MancalaPlayer.PlayerOne.index, playerOneBankIndex)
    val playerTwoBoard : MutableList<Int>
        get() = board.subList(MancalaPlayer.PlayerTwo.index, playerTwoBankIndex)

    fun choosePit(player: MancalaPlayer, pit: MancalaPlayerPit) {
        /**
         * Plays the [pit] chosen by the [player]
         *
         * If it is not players turn its throws an [InvalidPlayerTurnException] exception
         */
        if (player != playerTurn) {
            throw InvalidPlayerTurnException()
        }
        val lastPitIndex = distributeStones(player, pit.index + player.index)

        if (hasEnded()) {
            collectStones()
        } else {
            checkAndCaptureStones(lastPitIndex, player)

            // If last pit was not in the [player] own bank we change the turn to the other player
            if(!(pitIsAPlayerBank(lastPitIndex) && pitIsInPlayerBoard(lastPitIndex, player))) {
                playerTurn = if(player == MancalaPlayer.PlayerOne) MancalaPlayer.PlayerTwo else MancalaPlayer.PlayerOne
            }
        }
    }

    fun choosePitIndex(pitIndex: Int, playerTurn: MancalaPlayer) {
        /**
         * Calls [choosePit]
         *
         * It's a nice method to have to fill automatically the history of the game
         */
        val pit = when (pitIndex) {
            1 -> MancalaPlayerPit.First
            2 -> MancalaPlayerPit.Second
            3 -> MancalaPlayerPit.Third
            4 -> MancalaPlayerPit.Fourth
            5 -> MancalaPlayerPit.Fifth
            6 -> MancalaPlayerPit.Sixth
            else -> {
                throw InvalidPitIndexException()
            }
        }
        choosePit(playerTurn, pit)
    }

    fun choosePitIndexAutoPlayer(pitIndex: Int) {
        /**
         * Calls [choosePit] with the player in [playerTurn]
         *
         * It's a nice method to have to fill automatically the history of the game
         */

        choosePitIndex(pitIndex, playerTurn)
    }

    private fun distributeStones(player: MancalaPlayer, pit: Int): Int {
        /**
         * It distributes the stones in the [pit] chosen by the [player]
         */
        val pitStonesCount = board[pit]
        if (pitStonesCount == 0) {
            throw EmptyPitException()
        }

        // Remove all stones from the current pit
        board[pit] = 0

        // Move to the next pit
        var pitIndex =  pit

        var stoneNumber = pitStonesCount
        while(stoneNumber > 0) {
            pitIndex += 1
            pitIndex %= board.size

            // We don't store stones in the opponents bank
            if ((player == MancalaPlayer.PlayerOne && pitIndex == playerTwoBankIndex) ||
                (player == MancalaPlayer.PlayerTwo && pitIndex == playerOneBankIndex)) {
                continue
            }

            board[pitIndex] += 1
            stoneNumber -= 1
        }

        return pitIndex
    }

    private fun checkAndCaptureStones(lastPitIndex: Int, player: MancalaPlayer) {
        /**
         * Check if the last stone lands in a Player empty pit. If that is the case we capture that stone and all
         * the stones in the opposing opponent side
         */
        val wasEmptyPit = board[lastPitIndex] == 1
        if (wasEmptyPit &&
            pitIsInPlayerBoard(lastPitIndex, player) &&
            !pitIsAPlayerBank(lastPitIndex)) {
            val oppositePitStoneIndex = board.size - lastPitIndex - 2
            val opponentPitStones = board[oppositePitStoneIndex]
            board[oppositePitStoneIndex] = 0
            board[lastPitIndex] = 0

            val playerBank = if (player == MancalaPlayer.PlayerOne) playerOneBankIndex else playerTwoBankIndex
            board[playerBank] += opponentPitStones + 1
        }
    }

    fun hasEnded(): Boolean {
        /**
         * Return if a game has ended. One of the player should have zero stones in its board
         */
        return playerOneBoard.sum() == 0 || playerTwoBoard.sum() == 0
    }

    private fun pitIsAPlayerBank(pitIndex: Int): Boolean {
        /**
         * Returns if the pit index is one of the players banks
         */
        return pitIndex == playerOneBankIndex || pitIndex == playerTwoBankIndex
    }

    private fun pitIsInPlayerBoard(pitIndex: Int, player: MancalaPlayer): Boolean {
        /**
         * Returns if the last pit was in the [player] board
         */
        return (player == MancalaPlayer.PlayerOne && pitIndex <  MancalaPlayer.PlayerTwo.index) ||
               (player == MancalaPlayer.PlayerTwo && pitIndex >= MancalaPlayer.PlayerTwo.index)
    }

    private fun emptyBoard(player: MancalaPlayer) {
        /**
         * Empties the [player] board
         */
        val from: Int
        val to: Int
        if (player == MancalaPlayer.PlayerOne) {
            from = MancalaPlayer.PlayerOne.index
            to = playerOneBankIndex - 1
        } else {
            from = MancalaPlayer.PlayerTwo.index
            to = playerTwoBankIndex - 1
        }

        for (i in from..to){
            board[i] = 0
        }
    }

    private fun collectStones() {
        /**
         * Collects all the stones in the pits of and put them in the players bank
         */
        playerOneBank += playerOneBoard.sum()
        emptyBoard(MancalaPlayer.PlayerOne)

        playerTwoBank += playerTwoBoard.sum()
        emptyBoard(MancalaPlayer.PlayerTwo)
    }
}

