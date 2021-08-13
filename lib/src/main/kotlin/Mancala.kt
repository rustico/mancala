package lib

/**
 * Main game of Mancala.
 *
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

    fun choosePit(player: MancalaPlayer, pit: MancalaPlayerPit) {
        /**
         * Plays the pit chosen by the player
         *
         * If it is not player turn its throws an [InvalidPlayerTurn] exception
         *
         * @param player: Player that is playing this move
         * @param pit: Pit or hole chosen by the player that will distribute its stones
         */
        if (player != playerTurn) {
            throw InvalidPlayerTurn()
        }
        val anotherTurn = distributeStones(player, pit.index + player.index)

        // Toggle player turn
        if (!anotherTurn) {
            playerTurn = if(player == MancalaPlayer.PlayerOne) MancalaPlayer.PlayerTwo else MancalaPlayer.PlayerOne
        }
    }

    private fun distributeStones(player: MancalaPlayer, pit: Int): Boolean {
        /**
         * It distributes the stones in the [pit] chosen by the [player]
         *
         * @param player: Player that is distributing the stones
         * @param pit: Pit or hole chosen by the player that will distribute its stones
         */
        val pitStonesCount = board[pit]

        // Remove all stones from the current pit
        board[pit] = 0

        var anotherTurn = false

        // Move to the next pit
        var pitIndex =  pit + 1

        for (stoneNumber in pitStonesCount downTo 1) {
            // We don't store stones in the opponents bank
            if(player == MancalaPlayer.PlayerOne && pitIndex == playerTwoBankIndex) {
               pitIndex += 1
            } else if(player == MancalaPlayer.PlayerTwo && pitIndex == playerOneBankIndex) {
               pitIndex += 1
            }

            pitIndex %= board.size

            val inOwnBoard =
                (player == MancalaPlayer.PlayerOne && pitIndex <  MancalaPlayer.PlayerTwo.index) ||
                (player == MancalaPlayer.PlayerTwo && pitIndex >= MancalaPlayer.PlayerTwo.index)

            // Check if the last stone lands in a Player empty pit. If that is the case we capture that stone and all
            // the stones in the opposing opponent side
            val emptyPit = board[pitIndex] == 0
            val lastStone = stoneNumber == 1
            val pitIsAPlayerBank = pitIndex == playerOneBankIndex || pitIndex == playerTwoBankIndex
            if (lastStone &&
                emptyPit &&
                inOwnBoard &&
                !pitIsAPlayerBank) {
                val oppositePitStoneIndex = (pitIndex + 7) % board.size
                val opponentPitStones = board[oppositePitStoneIndex]
                board[oppositePitStoneIndex] = 0

                val playerBank = if(player == MancalaPlayer.PlayerOne) playerOneBankIndex else playerTwoBankIndex
                board[playerBank] = opponentPitStones + stoneNumber

            } else {
                board[pitIndex] += 1

                if(lastStone && pitIsAPlayerBank) {
                    anotherTurn = true
                }
            }
            pitIndex += 1
        }

        return anotherTurn
    }
}
