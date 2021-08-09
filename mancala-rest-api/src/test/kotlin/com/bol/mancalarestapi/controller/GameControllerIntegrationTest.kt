package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
internal class GameControllerIntegrationTest(
    @Autowired val client: TestRestTemplate,
    @Autowired val jdbc: JdbcTemplate
) {
    companion object {
        @Container
        val container = postgres("13-alpine") {
            withDatabaseName("db")
            withUsername("user")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }
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
}