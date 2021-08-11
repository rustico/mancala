package lib

class MancalaGame (
    val playerOne : String,
    val playerTwo : String,
    var turn: MancalaPlayer = MancalaPlayer.PlayerOne,
    val numberOfStones : Int = 6
) {
    // Divide the main board in two.
    val board = MutableList(14) { if (it == 6 || it == 13) 0 else numberOfStones }

    fun choosePit(player: MancalaPlayer, pit: MancalaPlayerPit) {
        distributeStones(player, pit.index + player.index)
    }

    private fun distributeStones(player: MancalaPlayer, pit: Int) {
        val pitStones = board[pit]

        // Remove all stones from the current pit
        board[pit] = 0

        // Move to the next pit
        var pitIndex =  pit + 1
        for (i in pitStones downTo 1) {
            // We don't store stones in the opponents bank
            if(player == MancalaPlayer.PlayerOne && pitIndex == MancalaPlayer.PlayerTwo.index + numberOfStones) {
               pitIndex += 1
            } else if(player == MancalaPlayer.PlayerTwo && pitIndex == MancalaPlayer.PlayerOne.index + numberOfStones) {
               pitIndex += 1
            }

            pitIndex %= board.size

            // Check if the last stone lands in a Player empty pit. If that is the case we capture that stone and all
            // the stones in the opposing opponent side
//            val pitStones = board[pitIndex]
//            if (pitStones == 0 && player == MancalaPlayer.PlayerOne && pitIndex < MancalaPlayer.PlayerTwo.index) {
//                val opponentPitStones = board[pitIndex + 7]
//                board[pitIndex + 7] = 0
//                board[6] = opponentPitStones +
//            }
            board[pitIndex] += 1
            pitIndex += 1
        }
    }
}
