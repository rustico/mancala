package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.JoinGameRequest
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.client.ResourceAccessException
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID


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
    fun `test we can create a new game and retrieve it`() {
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
        val response = client.getForEntity("/games", Array<GameResponse>::class.java)
        assertTrue(response.hasBody())
        assertEquals(1, response.body!!.size)
        val gameResponse = response.body!![0]
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
        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
        assertEquals(gameResponse.playerOne, playerOne)
        assertNull(gameResponse.playerTwo)
    }

    @Test
    fun `test we can't get a game by using an unknow uuid`() {
        // Create one game
        val playerOne = "Alice"
        val newGameRequest = NewGameRequest(playerOne = playerOne)
        client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Retrieve game
        val response = client.getForEntity("/games/${UUID.randomUUID()}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `test we can join a game`() {
        // Create one game
        val playerOne = "Alice"
        val newGameRequest = NewGameRequest(playerOne = playerOne)
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Join game
        val playerTwo = "Bob"
        val joinGameRequest = JoinGameRequest(playerTwo = playerTwo)
        client.put("/games/${newGameResponse.uuid}/join/${newGameResponse.apiKey}", joinGameRequest)

        // Retrieve game
        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
        assertEquals(gameResponse.playerOne, playerOne)
        assertEquals(gameResponse.playerTwo, playerTwo)
    }

    @Test
    fun `test we cannot join a game with two players already playing`() {
        // Create one game
        val newGameRequest = NewGameRequest(playerOne = "Alice")
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Join game
        val joinGameRequest = JoinGameRequest(playerTwo = "Bob")
        client.put(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.apiKey}",
            joinGameRequest
        )

        // Join Again
        assertThrows(ResourceAccessException::class.java) {
            client.exchange(
                "/games/${newGameResponse.uuid}/join/${newGameResponse.apiKey}",
                HttpMethod.PUT,
                HttpEntity<JoinGameRequest>(joinGameRequest),
                String::class.java
            )
        }
    }

    @Test
    fun `test we cannot join a game with an invalid api key`() {
        // Create one game
        val newGameRequest = NewGameRequest(playerOne = "Alice")
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Join game
        val joinGameRequest = JoinGameRequest(playerTwo = "Not Bob")
        assertThrows(ResourceAccessException::class.java) {
            client.exchange(
                "/games/${newGameResponse.uuid}/join/${UUID.randomUUID()}",
                HttpMethod.PUT,
                HttpEntity<JoinGameRequest>(joinGameRequest),
                String::class.java
            )
        }
    }

    @Test
    fun `test we cannot join a game with an unknown game uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(playerOne = "Alice")
        val newGameResponse = client.postForObject("/games", newGameRequest, NewGameResponse::class.java)

        // Join game
        val joinGameRequest = JoinGameRequest(playerTwo = "Not Bob")
        val response = client.exchange(
            "/games/${UUID.randomUUID()}/join/${newGameResponse.apiKey}",
            HttpMethod.PUT,
            HttpEntity<JoinGameRequest>(joinGameRequest),
            String::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}