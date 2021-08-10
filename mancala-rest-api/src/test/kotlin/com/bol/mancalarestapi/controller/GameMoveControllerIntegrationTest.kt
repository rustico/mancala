package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameMoveResponse
import org.junit.jupiter.api.Assertions.*
import com.bol.mancalarestapi.dto.NewGameMoveRequest
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
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
        val playerOne = "Bob"
        val newGameRequest = NewGameRequest(playerOneName = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            apiKey = newGameResponse.apiKey,
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
        val playerOne = "Bob"
        val newGameRequest = NewGameRequest(playerOneName = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            apiKey = UUID.randomUUID(),
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
            apiKey = UUID.randomUUID(),
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
        val playerOne = "Bob"
        val newGameRequest = NewGameRequest(playerOneName = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Create a move
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            apiKey = newGameResponse.apiKey,
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
}