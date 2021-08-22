package com.bol.api.service

import com.bol.api.dto.GameMoveResponse
import com.bol.api.dto.GameResponse
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.extension.toGameMoveResponse
import com.bol.api.extension.toGameResponse
import com.bol.api.model.GameMove
import com.bol.api.repository.GameMoveRepository
import com.bol.api.repository.GameRepository
import lib.MancalaPlayer
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID
import javax.transaction.Transactional

@Service
class GameMoveService(
    private val gameMoveRepository: GameMoveRepository,
    private val gameRepository: GameRepository,
    private val mancalaService: MancalaService
) {
    fun findAllByGameUuid(gameUuid: UUID): List<GameMoveResponse> {
        /**
         * Get all moves by [gameUuid] order by id
         */
        return gameMoveRepository
            .findAllByGameUuidOrderById(gameUuid)
            .mapIndexed() { index, gameMove -> gameMove.toGameMoveResponse(index) }
    }

    @Transactional
    fun create(newGameMoveRequest: NewGameMoveRequest): GameResponse {
        /**
         * Creates a new move in the Game
         */
        try {
            val game = gameRepository.findByUuid(newGameMoveRequest.gameUuid)
            val gameMoves = gameMoveRepository.findAllByGameUuidOrderById(newGameMoveRequest.gameUuid)
            val mancalaGame = mancalaService.getMancalaGame(game.numberOfStones, gameMoves)

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

            val gameMove = GameMove(
                game = game,
                position = newGameMoveRequest.position
            )

            gameMoveRepository.save(gameMove)

            if (mancalaGame.hasEnded()) {
                // TODO: refactor
                game.winner = if (mancalaGame.playerOneBank > mancalaGame.playerTwoBank) 1 else 2
                gameRepository.save(game)
            }

            return game.toGameResponse(mancalaGame)
        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}
