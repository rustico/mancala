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
         * If it is not players turn its throws an [InvalidPlayerTurn] exception
         */
        if (player != playerTurn) {
            throw InvalidPlayerTurn()
        }
        val lastPitIndex = distributeStones(player, pit.index + player.index)

        if (hasEnded()) {
            collectStonesTo(player)
        } else {
            captureStones(lastPitIndex, player)

            // If last pit was not in the [player] own bank we toggle the turn to the other player
            if(!(pitIsAPlayerbank(lastPitIndex) && pitIsInPlayerBoard(lastPitIndex, player))) {
                playerTurn = if(player == MancalaPlayer.PlayerOne) MancalaPlayer.PlayerTwo else MancalaPlayer.PlayerOne
            }
        }
    }

    fun choosePitAutoPlayer(pit: MancalaPlayerPit) {
        /**
         * Calls [choosePit] with the player in [playerTurn]
         *
         * It's a nice method to have to fill automatically the history of the game
         */
        choosePit(playerTurn, pit)
    }

    private fun distributeStones(player: MancalaPlayer, pit: Int): Int {
        /**
         * It distributes the stones in the [pit] chosen by the [player]
         */
        val pitStonesCount = board[pit]
        if (pitStonesCount == 0) {
            throw EmptyPit()
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

    private fun captureStones(lastPitIndex: Int, player: MancalaPlayer) {
        /**
         * Check if the last stone lands in a Player empty pit. If that is the case we capture that stone and all
         * the stones in the opposing opponent side
         */
        val wasEmptyPit = board[lastPitIndex] == 1
        if (wasEmptyPit &&
            pitIsInPlayerBoard(lastPitIndex, player) &&
            !pitIsAPlayerbank(lastPitIndex)) {
            val oppositePitStoneIndex = (lastPitIndex + 7) % board.size
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

    private fun pitIsAPlayerbank(pitIndex: Int): Boolean {
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
         * Empty the [player] board
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

    private fun collectStonesTo(player: MancalaPlayer) {
        /**
         * Collects all the stones in the pits of the opposite player to the [player] bank
         */
        if (player == MancalaPlayer.PlayerOne) {
            playerOneBank += playerTwoBoard.sum()
            emptyBoard(MancalaPlayer.PlayerTwo)
        } else {
            playerTwoBank += playerOneBoard.sum()
            emptyBoard(MancalaPlayer.PlayerOne)
        }
    }
}

