package com.bol.api.service

import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.extension.toGameResponse
import com.bol.api.model.Game
import com.bol.api.model.GameMove
import com.bol.api.repository.GameMoveRepository
import com.bol.api.repository.GameRepository
import io.mockk.every
import io.mockk.mockk
import lib.InvalidPlayerTurnException
import lib.MancalaGame
import lib.MancalaPlayer
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.dao.EmptyResultDataAccessException
import java.util.UUID

internal class GameMoveServiceTest {

    @Test
    fun `test it should play a move in the game`() {
        val gameRepository = mockk<GameRepository>()
        val game = Game()
        every { gameRepository.findByUuid(game.uuid) } returns game

        val gameMoveRepository = mockk<GameMoveRepository>()
        every { gameMoveRepository.findAllByGameUuidOrderById(game.uuid)} returns mutableListOf<GameMove>()
        val gameMove = GameMove(
            game = game,
            position = 3
        )
        every { gameMoveRepository.save(any()) } returns gameMove

        val mancalaGame = MancalaGame()
        val mancalaService = mockk<MancalaService>()
        every { mancalaService.getMancalaGame(any(), any()) } returns mancalaGame

        val gameMoveService = GameMoveService(gameMoveRepository, gameRepository, mancalaService)
        val newGameMoveRequest = NewGameMoveRequest(
            position = gameMove.position,
            playerApiKey =  game.playerOneApiKey,
            gameUuid = game.uuid
        )
        val gameResponse = gameMoveService.create(newGameMoveRequest)
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
    fun `test it should throw GameNotFoundException when game is not found`() {
        val gameRepository = mockk<GameRepository>()
        val uuid = UUID.randomUUID()
        every { gameRepository.findByUuid(uuid) } throws EmptyResultDataAccessException(1)

        val gameMoveRepository = mockk<GameMoveRepository>()
        val mancalaService = mockk<MancalaService>()

        val gameMoveService = GameMoveService(gameMoveRepository, gameRepository, mancalaService)
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey = UUID.randomUUID(),
            gameUuid = uuid

        )

        assertThrows(GameNotFoundException::class.java) {
            gameMoveService.create(newGameMoveRequest)
        }
    }

    @Test
    fun `test it should throw InvalidPlayerTurnException when is not player turn`() {
        val gameRepository = mockk<GameRepository>()
        val game = Game()
        every { gameRepository.findByUuid(game.uuid) } returns game

        val gameMoveRepository = mockk<GameMoveRepository>()
        every { gameMoveRepository.findAllByGameUuidOrderById(game.uuid)} returns mutableListOf<GameMove>()

        val mancalaGame = MancalaGame(playerTurn = MancalaPlayer.PlayerTwo)
        val mancalaService = mockk<MancalaService>()
        every { mancalaService.getMancalaGame(any(), any()) } returns mancalaGame

        val gameMoveService = GameMoveService(gameMoveRepository, gameRepository, mancalaService)
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey =  game.playerOneApiKey,
            gameUuid = game.uuid
        )

        assertThrows(InvalidPlayerTurnException::class.java) {
            gameMoveService.create(newGameMoveRequest)
        }
    }

    @Test
    fun `test it should throw InvalidAPIKeyException when the request is made with an invalid API Key`() {
        val gameRepository = mockk<GameRepository>()
        val game = Game()
        every { gameRepository.findByUuid(game.uuid) } returns game

        val gameMoveRepository = mockk<GameMoveRepository>()
        every { gameMoveRepository.findAllByGameUuidOrderById(game.uuid)} returns mutableListOf<GameMove>()

        val mancalaGame = MancalaGame(playerTurn = MancalaPlayer.PlayerTwo)
        val mancalaService = mockk<MancalaService>()
        every { mancalaService.getMancalaGame(any(), any()) } returns mancalaGame

        val gameMoveService = GameMoveService(gameMoveRepository, gameRepository, mancalaService)
        val newGameMoveRequest = NewGameMoveRequest(
            position = 1,
            playerApiKey =  UUID.randomUUID(),
            gameUuid = game.uuid
        )

        assertThrows(InvalidAPIKeyException::class.java) {
            gameMoveService.create(newGameMoveRequest)
        }
    }
}