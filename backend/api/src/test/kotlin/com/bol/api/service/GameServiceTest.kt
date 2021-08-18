package com.bol.api.service

import com.bol.api.extension.toGameResponse
import com.bol.api.model.Game
import com.bol.api.repository.GameRepository
import io.mockk.every
import lib.MancalaGame
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.UUID
import io.mockk.mockk

internal class GameServiceTest() {

    @Test
    fun `test should find game by its UUID`() {
        val gameRepository = mockk<GameRepository>()
        val game = Game()
        every { gameRepository.findByUuid(game.uuid) } returns game

        val mancalaGame = MancalaGame()
        val mancalaService= mockk<MancalaService>()
        every { mancalaService.getMancalaGame(game.uuid) } returns mancalaGame

        val gameService = GameService(gameRepository, mancalaService)
        val gameResponse = gameService.findByUuid(game.uuid)

        val expectedGameResponse = game.toGameResponse(mancalaGame)
        assertEquals(expectedGameResponse.uuid, gameResponse.uuid)
        assertEquals(expectedGameResponse.createdAt, gameResponse.createdAt)
        assertEquals(expectedGameResponse.updatedAt, gameResponse.updatedAt)
        assertEquals(expectedGameResponse.playerOneBoard, gameResponse.playerOneBoard)
        assertEquals(expectedGameResponse.playerOneBank, gameResponse.playerOneBank)
        assertEquals(expectedGameResponse.playerOneId, gameResponse.playerOneId)
        assertEquals(expectedGameResponse.playerTwoBoard, gameResponse.playerTwoBoard)
        assertEquals(expectedGameResponse.playerTwoBank, gameResponse.playerTwoBank)
        assertEquals(expectedGameResponse.playerTwoId, gameResponse.playerTwoId)
        assertEquals(expectedGameResponse.playerTurn, gameResponse.playerTurn)
    }

    @Test
    fun `test it should throw GameNotFoundException when MancalaService doesn't find game by its UUID`() {
        val gameRepository = mockk<GameRepository>()
        val game = Game()
        every { gameRepository.findByUuid(game.uuid) } returns Game()

        val mancalaService = mockk<MancalaService>()

        val gameService = GameService(gameRepository, mancalaService)

        val randomUuid = UUID.randomUUID()
        every { mancalaService.getMancalaGame(randomUuid) } throws GameNotFoundException()

        assertThrows(GameNotFoundException::class.java) {
            gameService.findByUuid(randomUuid)
        }
    }

    @Test
    fun `test it should throw GameNotFoundException when GameService doesn't find game by its UUID`() {
        val gameRepository = mockk<GameRepository>()
        val mancalaService = mockk<MancalaService>()

        val gameService = GameService(gameRepository, mancalaService)

        val randomUuid = UUID.randomUUID()
        every { gameRepository.findByUuid(randomUuid) } throws GameNotFoundException()
        assertThrows(GameNotFoundException::class.java) {
            gameService.findByUuid(randomUuid)
        }
    }
}