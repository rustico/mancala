package com.bol.api.service

import org.springframework.stereotype.Service
import com.bol.api.dto.MancalaGameResponse
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.repository.GameMoveRepository
import com.bol.api.repository.GameRepository
import lib.MancalaGame
import lib.MancalaPlayer
import org.springframework.dao.EmptyResultDataAccessException

@Service
class MancalaService(
    private val gameMoveRepository: GameMoveRepository,
    private val gameRepository: GameRepository) {
    fun getMancalaGame(newGameMoveRequest: NewGameMoveRequest) : MancalaGame {
        /**
         * Returns MancalaGame with its history of moves loaded
         */
        val gameMoves = gameMoveRepository.findAllByGameUuidOrderById(newGameMoveRequest.gameUuid)
        val mancalaGame = MancalaGame()
        for (gameMove in gameMoves) {
            mancalaGame.choosePitIndexAutoPlayer(gameMove.position)
        }

        return mancalaGame
    }

    fun playMove(mancalaGame: MancalaGame, newGameMoveRequest: NewGameMoveRequest): MancalaGameResponse {
        /**
         * Plays the move in the MancalaGame
         */
        try {
            val game = gameRepository.findByUuid(newGameMoveRequest.gameUuid)
            val mancalaPlayer = when (newGameMoveRequest.playerApiKey) {
                game.playerOneApiKey -> {
                    MancalaPlayer.PlayerOne
                }
                game.playerTwoApiKey -> {
                    MancalaPlayer.PlayerTwo
                }
                else -> {
                    throw InvalidAPIKeyException()
                }
            }

            mancalaGame.choosePitIndex(newGameMoveRequest.position, mancalaPlayer)

            return MancalaGameResponse(
                playerOneBoard = mancalaGame.playerOneBoard,
                playerOneBank = mancalaGame.playerOneBank,
                playerTwoBoard = mancalaGame.playerTwoBoard,
                playerTwoBank = mancalaGame.playerTwoBank
            )
        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}