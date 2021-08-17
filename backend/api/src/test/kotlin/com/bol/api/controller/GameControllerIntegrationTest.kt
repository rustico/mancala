package com.bol.api.controller

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameRequest
import com.bol.api.dto.NewGameResponse
import com.bol.api.dto.SimpleGameResponse
import com.bol.api.utils.shortUuid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
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
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        assertNotNull(newGameResponse.apiKey)
        assertNotNull(newGameResponse.invitationApiKey)
        assertNotNull(newGameResponse.uuid)
        assertNotNull(newGameResponse.createdAt)
        assertNotNull(newGameResponse.updatedAt)

        // Retrieve game
        val response = client.getForEntity("/games", Array<SimpleGameResponse>::class.java)
        assertTrue(response.hasBody())
        assertEquals(1, response.body!!.size)
        val gameResponse = response.body!![0]
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
    }

    @Test
    fun `test if we can get a game by its uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        // Retrieve game
        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)
        assertNotNull(gameResponse.uuid)
        assertNotNull(gameResponse.createdAt)
        assertNotNull(gameResponse.updatedAt)
    }

    @Test
    fun `test we can't get a game by using an unknow uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )

        client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        // Retrieve game
        val response = client.getForEntity("/games/${UUID.randomUUID()}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `test we can join a game`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)


        // Join game
        val joinGameResponse = client.getForObject(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.invitationApiKey}",
            JoinGameResponse::class.java)

        assertNotNull(joinGameResponse.uuid)
        assertNotNull(newGameResponse.apiKey)
        assertNotNull(joinGameResponse.createdAt)
        assertNotNull(joinGameResponse.updatedAt)
    }

    @Test
    fun `test we cannot join a game that has already started`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)


        // Join game
        client.getForObject(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.invitationApiKey}",
            JoinGameResponse::class.java)

        // Join Again
        val response = client.getForEntity(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.invitationApiKey}",
            String::class.java)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `test we cannot join a game with an invalid api key`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)


        // Join game
        val response = client.getForEntity(
            "/games/${newGameResponse.uuid}/join/${UUID.randomUUID()}",
            String::class.java)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `test we cannot join a game with an unknown game uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)


        // Join game
        val response = client.getForEntity(
            "/games/${UUID.randomUUID()}/join/${newGameResponse.invitationApiKey}",
            String::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `test we get a game board by its uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        // Retrieve game
        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)

        assertTrue(intArrayOf(6, 6, 6, 6, 6, 6).contentEquals(gameResponse.playerOneBoard.toIntArray()))
        assertEquals(0, gameResponse.playerOneBank)
        assertTrue(intArrayOf(6, 6, 6, 6, 6, 6).contentEquals(gameResponse.playerTwoBoard.toIntArray()))
        assertEquals(0, gameResponse.playerTwoBank)
    }

    @Test
    fun `test we get ids for PlayerOne and PlayerTwo when getting a game by its uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        // Join game
        val joinGameResponse = client.getForObject(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.invitationApiKey}",
            JoinGameResponse::class.java)

        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)

        assertEquals(shortUuid(newGameResponse.apiKey), gameResponse.playerOneId)
        assertEquals(shortUuid(joinGameResponse.apiKey), gameResponse.playerTwoId)
    }

    @Test
    fun `test we get whose turn is when getting a game by its uuid`() {
        // Create one game
        val newGameRequest = NewGameRequest(
            numberOfStones = 6
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        // Join game
        client.getForObject(
            "/games/${newGameResponse.uuid}/join/${newGameResponse.invitationApiKey}",
            JoinGameResponse::class.java)

        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)

        assertEquals(shortUuid(newGameResponse.apiKey), gameResponse.playerTurn)
    }

    @Test
    fun `test we can set the number of stones for the game`() {
        // Create one game
        val numberOfStones = 4
        val newGameRequest = NewGameRequest(
            numberOfStones = numberOfStones
        )
        val newGameResponse = client.postForObject(
            "/games/new",
            newGameRequest,
            NewGameResponse::class.java)

        val gameResponse = client.getForObject("/games/${newGameResponse.uuid}", GameResponse::class.java)

        val numberOfPIts = 6
        assertEquals(numberOfStones * numberOfPIts, gameResponse.playerOneBoard.sum())
    }
}