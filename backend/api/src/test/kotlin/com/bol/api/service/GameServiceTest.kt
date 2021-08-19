package com.bol.api.service

import com.bol.api.extension.toGameResponse
import com.bol.api.model.Game
import com.bol.api.model.GameMove
import com.bol.api.repository.GameMoveRepository
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

        val gameMoveRepository = mockk<GameMoveRepository>()
        every { gameMoveRepository.findAllByGameUuidOrderById(game.uuid)} returns mutableListOf<GameMove>()

        val mancalaGame = MancalaGame()
        val mancalaService = mockk<MancalaService>()
        every { mancalaService.getMancalaGame(any(), any()) } returns mancalaGame

        val gameService = GameService(gameRepository, gameMoveRepository, mancalaService)
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
        fun `test it should throw GameNotFoundException when game is not found by its UUID`() {
            val gameRepository = mockk<GameRepository>()
            val game = Game()
            every { gameRepository.findByUuid(any()) } throws GameNotFoundException()

            val gameMoveRepository = mockk<GameMoveRepository>()
            every { gameMoveRepository.findAllByGameUuidOrderById(game.uuid)} returns mutableListOf<GameMove>()

            val mancalaGame = MancalaGame()
            val mancalaService = mockk<MancalaService>()
            every { mancalaService.getMancalaGame(any(), any()) } returns mancalaGame

            val gameService = GameService(gameRepository, gameMoveRepository, mancalaService)

            assertThrows(GameNotFoundException::class.java) {
                gameService.findByUuid(UUID.randomUUID())
            }
        }
}