package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
class GameControllerIntegrationTest(
    @Autowired val client: TestRestTemplate,
    @Autowired val jdbc: JdbcTemplate
) : PostgresContainer() {
    @AfterEach
    fun cleanup() {
        jdbc.execute("delete from games")
    }

    @Test
    fun `test if we can create a new game and retrieve it`() {
        // No games
        val emptyEntity = client.getForEntity("/games", List::class.java)
        assertTrue(emptyEntity.hasBody())
        assertEquals(0, emptyEntity.body?.size)

        // Create one game
        val playerOne = "Bob"
        val newGameRequest = NewGameRequest(playerOne = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)
        assertNotNull(newGameResponse.apiKey)
        assertNotNull(newGameResponse.uuid)
        assertNotNull(newGameResponse.createdAt)
        assertNotNull(newGameResponse.updatedAt)
        assertEquals(newGameResponse.playerOne, playerOne)
        assertNull(newGameResponse.playerTwo)

        // Retrieve game
        val oneEntity = client.getForEntity("/games", Array<GameResponse>::class.java)
        assertTrue(oneEntity.hasBody())
        assertEquals(1, oneEntity.body!!.size)
        val gameResponse = oneEntity.body!![0]
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
        assertEquals(gameResponse.playerOne, playerOne)
        assertNull(gameResponse.playerTwo)
    }

    @Test
    fun `test if we can get a game by its uuid`() {
        // Create one game
        val playerOne = "Alice"
        val newGameRequest = NewGameRequest(playerOne = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Retrieve game
        val gameResponse = client.getForObject("/games/" + newGameResponse.uuid, GameResponse::class.java)
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
        assertEquals(gameResponse.playerOne, playerOne)
        assertNull(gameResponse.playerTwo)
    }
}