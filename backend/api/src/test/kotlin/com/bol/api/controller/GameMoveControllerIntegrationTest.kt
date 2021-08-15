package com.bol.api.controller

import com.bol.api.dto.GameMoveResponse
import com.bol.api.dto.MancalaGameResponse
import org.junit.jupiter.api.Assertions.*
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.dto.NewGameResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.client.ResourceAccessException
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
class GameMoveControllerIntegrationTest(
    @Autowired val client: TestRestTemplate,
    @Autowired val jdbc: JdbcTemplate
) : PostgresContainer() {
    @AfterEach
    fun cleanup() {
        jdbc.execute("delete from games_moves")
        jdbc.execute("delete from games")
    }

    @Test
    fun `test we can create a new game move`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        val gameMoveResponse = client.postForEntity(
            "/gamesmoves",
            newGameMoveRequest,
            Any::class.java
        )

        assertEquals(HttpStatus.OK, gameMoveResponse.statusCode)
    }

    @Test
    fun `test we cannot create a new game move without a valid api key`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = UUID.randomUUID(),
            gameUuid = newGameResponse.uuid
        )
        assertThrows(ResourceAccessException::class.java) {
            client.postForEntity(
                "/gamesmoves",
                newGameMoveRequest,
                Any::class.java
            )
        }
    }

    @Test
    fun `test we cannot create a new game move to an unknown game`() {
        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = UUID.randomUUID(),
            gameUuid = UUID.randomUUID()
        )

        val response = client.postForEntity(
            "/gamesmoves",
            newGameMoveRequest,
            String::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `test we can get all game moves`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        client.postForEntity(
            "/gamesmoves",
            newGameMoveRequest,
            Any::class.java
        )

        // Get moves
        val response = client.getForEntity(
            "/gamesmoves/${newGameResponse.uuid}",
            Array<GameMoveResponse>::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.hasBody())
        assertEquals(1, response.body!!.size)
        val gameMoveResponse = response.body!![0]
        assertNotNull(gameMoveResponse.position)
        assertNotNull(gameMoveResponse.createdAt)
        assertNotNull(gameMoveResponse.turn)
    }

    @Test
    fun `test when can create a new game move we received the new board state`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        val response = client.postForEntity(
            "/gamesmoves",
            newGameMoveRequest,
            MancalaGameResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(intArrayOf(0, 7, 7, 7, 7, 7).contentEquals(response.body!!.playerOneBoard.toIntArray()))
        assertEquals(1, response.body!!.playerOneBank)
        assertTrue(intArrayOf(6, 6, 6, 6, 6, 6).contentEquals(response.body!!.playerTwoBoard.toIntArray()))
        assertEquals(0, response.body!!.playerTwoBank)
    }

    @Test
    fun `test when creating multiples game moves the board maintains its state`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val playerOneGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        client.postForEntity(
            "/gamesmoves",
            playerOneGameMoveRequest,
            MancalaGameResponse::class.java
        )

        // Create another move
        val playerOneGameSecondMoveRequest = NewGameMoveRequest(
            position = 2,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        val response = client.postForEntity(
            "/gamesmoves",
            playerOneGameSecondMoveRequest,
            MancalaGameResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(intArrayOf(0, 0, 8, 8, 8, 8).contentEquals(response.body!!.playerOneBoard.toIntArray()))
        assertEquals(2, response.body!!.playerOneBank)
        assertTrue(intArrayOf(7, 7, 6, 6, 6, 6).contentEquals(response.body!!.playerTwoBoard.toIntArray()))
        assertEquals(0, response.body!!.playerTwoBank)
    }

    @Test
    fun `test PlayerOne cannot play when is not his turn`() {
        // Create one game
        val newGameResponse = client.getForObject("/games/new", NewGameResponse::class.java)

        // Create a move
        val playerOneGameMoveRequest = NewGameMoveRequest(
            position = 2,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        client.postForEntity(
            "/gamesmoves",
            playerOneGameMoveRequest,
            MancalaGameResponse::class.java
        )

        // Create another move
        val playerOneGameSecondMoveRequest = NewGameMoveRequest(
            position = 3,
            playerApiKey = newGameResponse.apiKey,
            gameUuid = newGameResponse.uuid
        )

        assertThrows(ResourceAccessException::class.java) {
            client.postForEntity(
                "/gamesmoves",
                playerOneGameSecondMoveRequest,
                Any::class.java
            )
        }
    }
}