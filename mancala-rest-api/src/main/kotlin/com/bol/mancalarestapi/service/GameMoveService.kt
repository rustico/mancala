package com.bol.mancalarestapi.service

import com.bol.mancalarestapi.dto.GameMoveResponse
import com.bol.mancalarestapi.dto.NewGameMoveRequest
import com.bol.mancalarestapi.extension.toGameMoveResponse
import com.bol.mancalarestapi.model.GameMove
import com.bol.mancalarestapi.repository.GameMoveRepository
import com.bol.mancalarestapi.repository.GameRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameMoveService(
    private val gameMoveRepository: GameMoveRepository,
    private val gameRepository: GameRepository
) {
    fun findAllByGameUuid(gameUuid: UUID): List<GameMoveResponse> {
        return gameMoveRepository
            .findAllByGameUuidOrderById(gameUuid)
            .mapIndexed() { index, gameMove -> gameMove.toGameMoveResponse(index) }
    }

    fun create(newGameMoveRequest: NewGameMoveRequest) {
        try {
            val game = gameRepository.findByUuid(newGameMoveRequest.gameUuid)

            if (game.apiKey != newGameMoveRequest.apiKey) {
                throw InvalidAPIKeyException()
            }

            val gameMove = GameMove(
                game = game,
                position = newGameMoveRequest.position
            )

            gameMoveRepository.save(gameMove)
        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}
