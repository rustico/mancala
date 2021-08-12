package lib

class MancalaGame (
    val playerOne : String,
    val playerTwo : String,
    var turn: MancalaPlayer = MancalaPlayer.PlayerOne,
    val numberOfStones : Int = 6
) {
    // Divide the main board in two.
    val playerOneBank = 6
    val playerTwoBank = 13
    val board = MutableList(14) { if (it == playerOneBank || it == playerTwoBank) 0 else numberOfStones }

    fun choosePit(player: MancalaPlayer, pit: MancalaPlayerPit) {
        distributeStones(player, pit.index + player.index)
    }

    private fun distributeStones(player: MancalaPlayer, pit: Int) {
        val pitStonesCount = board[pit]

        // Remove all stones from the current pit
        board[pit] = 0

        // Move to the next pit
        var pitIndex =  pit + 1

        for (stoneNumber in pitStonesCount downTo 1) {
            // We don't store stones in the opponents bank
            if(player == MancalaPlayer.PlayerOne && pitIndex == MancalaPlayer.PlayerTwo.index + numberOfStones) {
               pitIndex += 1
            } else if(player == MancalaPlayer.PlayerTwo && pitIndex == MancalaPlayer.PlayerOne.index + numberOfStones) {
               pitIndex += 1
            }

            pitIndex %= board.size

            val inOwnBoard =
                (player == MancalaPlayer.PlayerOne && pitIndex <  MancalaPlayer.PlayerTwo.index) ||
                (player == MancalaPlayer.PlayerTwo && pitIndex >= MancalaPlayer.PlayerTwo.index)

            // Check if the last stone lands in a Player empty pit. If that is the case we capture that stone and all
            // the stones in the opposing opponent side
            val pitStones = board[pitIndex]
            if (stoneNumber == 1 &&
                pitStones == 0 &&
                inOwnBoard) {
                val oppositePitStoneIndex = (pitIndex + 7) % board.size
                val opponentPitStones = board[oppositePitStoneIndex]
                board[oppositePitStoneIndex] = 0

                val playerBank = if(player == MancalaPlayer.PlayerOne) playerOneBank else playerTwoBank
                board[playerBank] = opponentPitStones + stoneNumber

            } else {
                board[pitIndex] += 1
            }

            pitIndex += 1
        }
    }
}
