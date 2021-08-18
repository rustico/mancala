package com.bol.api.service

import com.bol.api.extension.toGameResponse
import com.bol.api.model.Game
import com.bol.api.repository.GameRepository
import lib.MancalaGame
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.time.LocalDateTime

internal class GameServiceTest() {

    @Test
    fun `test we should find game by its UUID`() {
        val gameRepository = Mockito.mock(GameRepository::class.java)
        val game = Game()
        Mockito.`when`(gameRepository.findByUuid(game.uuid)).thenReturn(game)

        val mancalaGame = MancalaGame()
        val mancalaService= Mockito.mock(MancalaService::class.java)
        Mockito.`when`(mancalaService.getMancalaGame(game.uuid)).thenReturn(mancalaGame)

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
}