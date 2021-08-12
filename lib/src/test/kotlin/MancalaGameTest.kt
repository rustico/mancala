import lib.InvalidPlayerTurn
import lib.MancalaGame
import lib.MancalaPlayer
import lib.MancalaPlayerPit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MancalaGameTest {
    @Test
    fun `test init game default`() {
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
        )

        assertEquals("Bob", mancalaGame.playerOne)
        assertEquals("Alice", mancalaGame.playerTwo)
        assertEquals(MancalaPlayer.PlayerOne, mancalaGame.playerTurn)
    }

    @Test
    fun `test init game with PlayerTwo starting`() {
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        assertEquals("Bob", mancalaGame.playerOne)
        assertEquals("Alice", mancalaGame.playerTwo)
        assertEquals(MancalaPlayer.PlayerTwo, mancalaGame.playerTurn)
    }

    @Test
    fun `test PlayerOne chooses first pit`() {
        /*
        Init
        [0]  6, 6, 6, 6, 6, 6*,
             6, 6, 6, 6, 6, 6   [0]

        Assert
        [1]  7, 7, 7, 7, 7, 0,
             6, 6, 6, 6, 6, 6  [0]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.First)
        assertTrue(intArrayOf(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerOne chooses second pit`() {
        /*
        Init
        [0]  6, 6, 6, 6, 6*, 6,
             6, 6, 6, 6, 6,  6 [0]

        Assert
        [1]  7, 7, 7, 7, 0, 6,
             7, 6, 6, 6, 6, 6 [0]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Second)
        assertTrue(intArrayOf(6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo chooses first pit`() {
        /*
        Init
        [0]  6,  6, 6, 6, 6, 6,
             6*, 6, 6, 6, 6, 6 [0]

        Assert
        [0]  6, 6, 6, 6, 6, 6,
             0, 7, 7, 7, 7, 7 [1]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.First)
        assertTrue(intArrayOf(6, 6, 6, 6, 6, 6, 0, 0, 7, 7, 7, 7, 7, 1).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo chooses sixth pit`() {
        /*
        Init
        [0]  6,  6, 6, 6, 6, 6,
             6, 6, 6, 6, 6,  6* [0]

        Assert
        [0]  6, 7, 7, 7, 7, 7,
             6, 6, 6, 6, 6, 6 [1]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Sixth)
        assertTrue(intArrayOf(7, 7, 7, 7, 7, 6, 0, 6, 6, 6, 6, 6, 0, 1).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerOne stones pass over the opponents bank without adding new stones to it`() {
        /*
        Init
        [0]  8*, 6, 6, 6, 6, 6,
             6,  6, 6, 6, 6, 6 [0]

        Assert
        [1]  0, 6, 6, 6, 6, 7,
             7, 7, 7, 7, 7, 7 [0]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        // Set 8 stones to PlayerOne sixth pit
        mancalaGame.board[MancalaPlayerPit.Sixth.index + MancalaPlayer.PlayerOne.index] = 8

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Sixth)
        assertTrue(intArrayOf(7, 6, 6, 6, 6, 0, 1, 7, 7, 7, 7, 7, 7, 0).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo stones pass over the opponents bank without adding new stones to it`() {
        /*
        Init
        [0]  6, 6, 6, 6, 6, 6,
             6, 6, 6, 6, 6, 8* [0]

        Assert
        [0]  7, 7, 7, 7, 7, 7,
             7, 6, 6, 6, 6, 0 [1]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        // Set 8 stones to PlayerOne sixth pit
        mancalaGame.board[MancalaPlayerPit.Sixth.index + MancalaPlayer.PlayerTwo.index] = 8

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Sixth)
        assertTrue(intArrayOf(7, 7, 7, 7, 7, 7, 0, 7, 6, 6, 6, 6, 0, 1).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerOne captures PlayerTwo stones because the last stone of the turn lands an empty pit`() {
        /*
        Init
        [0]  6, 6, 0, 1*, 6, 6,
             6, 6, 6, 6,  6, 6 [0]

        Assert
        [7]  6, 6, 0, 0, 6, 6,
             6, 6, 0, 6, 6, 6 [0]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        // Set initial stones
        mancalaGame.board[MancalaPlayerPit.Third.index + MancalaPlayer.PlayerOne.index] = 1
        mancalaGame.board[MancalaPlayerPit.Fourth.index + MancalaPlayer.PlayerOne.index] = 0

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Third)
        assertTrue(intArrayOf(6, 6, 0, 0, 6, 6, 7, 6, 6, 6, 0, 6, 6, 0).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo captures PlayerOne stones because the last stone of the turn lands an empty pit`() {
        /*
        Init
        [0]  6, 6, 6,  6, 6, 6,
             6, 6, 1*, 0, 6, 6 [0]

        Assert
        [7]  6, 6, 6, 0, 6, 6,
             6, 6, 0, 0, 6, 6 [7]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        // Set initial stones
        mancalaGame.board[MancalaPlayerPit.Third.index + MancalaPlayer.PlayerTwo.index] = 1
        mancalaGame.board[MancalaPlayerPit.Fourth.index + MancalaPlayer.PlayerTwo.index] = 0

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Third)
        assertTrue(intArrayOf(6, 6, 6, 0, 6, 6, 0, 6, 6, 0, 0, 6, 6, 7).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerOne doesn't captures PlayerTwo stones because the last stone of the turn lands in its own empty bank`() {
        // This rule only applies whe it lands it an empty pit. Not the bank.

        /*
        Init
        [0]  1, 6, 6, 6, 6, 6,
             6, 6, 6, 6, 6, 6 [1]

        Assert
        [1]  0, 6, 6, 6, 6, 6,
             6, 6, 6, 6, 6, 6 [1]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        // Set initial stones
        mancalaGame.board[MancalaPlayerPit.Sixth.index + MancalaPlayer.PlayerOne.index] = 1
        mancalaGame.playerTwoBank = 1

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Sixth)
        assertTrue(intArrayOf(6, 6, 6, 6, 6, 0, 1, 6, 6, 6, 6, 6, 6, 1).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo doesn't captures PlayerOne stones because the last stone of the turn lands in its own empty bank`() {
        // This rule only applies whe it lands it an empty pit. Not the bank.

        /*
        Init
        [1]  6, 6, 6, 6, 6, 6,
             6, 6, 6, 6, 6, 1* [0]

        Assert
        [1]  6, 6, 6, 6, 6, 6,
             6, 6, 6, 6, 6, 0 [1]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        // Set initial stones
        mancalaGame.board[MancalaPlayerPit.Sixth.index + MancalaPlayer.PlayerTwo.index] = 1
        mancalaGame.playerOneBank = 1

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Sixth)
        assertTrue(intArrayOf(6, 6, 6, 6, 6, 6, 1, 6, 6, 6, 6, 6, 0, 1).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo cannot play because is not his turn`() {
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )
        assertThrows(InvalidPlayerTurn::class.java) {
            mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Third)
        }
    }

    @Test
    fun `test PlayerOne play cannot two times because is not his turn`() {
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Third)

        assertThrows(InvalidPlayerTurn::class.java) {
            mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Fourth)
        }
    }

    @Test
    fun `test PlayerOne lands last stone in his bank so he gets another turn`() {
        /*
        Init: First turn
        [0]  6, 6, 6, 6, 6, 6*,
             6, 6, 6, 6, 6, 6  [0]

        Init: Second turn
        [1]  7, 7, 7, 7, 7*, 0,
             6, 6, 6, 6, 6 , 6 [0]

        Assert
        [2]  8, 8, 8, 8, 0, 0,
             7, 7, 6, 6, 6, 6  [0]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice"
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.First)
        mancalaGame.choosePit(MancalaPlayer.PlayerOne, MancalaPlayerPit.Second)
        assertTrue(intArrayOf(0, 0, 8, 8, 8, 8, 2, 7, 7, 6, 6, 6, 6, 0).contentEquals(mancalaGame.board.toIntArray()))
    }

    @Test
    fun `test PlayerTwo lands last stone in his bank so he gets another turn`() {
        /*
        Init: First turn
        [0]  6,  6, 6, 6, 6, 6,
             6*, 6, 6, 6, 6, 6  [0]

        Init: Second turn
        [1]  6, 6,  6, 6, 6, 6,
             0, 7*, 7, 7, 7, 7 [1]

        Assert
        [1]  6, 6, 6, 6, 7, 7,
             0, 0, 8, 8, 8, 8 [2]
        */
        val mancalaGame = MancalaGame(
            playerOne = "Bob",
            playerTwo = "Alice",
            playerTurn = MancalaPlayer.PlayerTwo
        )

        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.First)
        mancalaGame.choosePit(MancalaPlayer.PlayerTwo, MancalaPlayerPit.Second)
        assertTrue(intArrayOf(7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2).contentEquals(mancalaGame.board.toIntArray()))
    }
}