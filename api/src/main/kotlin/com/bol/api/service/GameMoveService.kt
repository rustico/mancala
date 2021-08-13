package com.bol.api.service

import com.bol.api.dto.GameMoveResponse
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.extension.toGameMoveResponse
import com.bol.api.model.GameMove
import com.bol.api.repository.GameMoveRepository
import com.bol.api.repository.GameRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameMoveService(
    private val gameMoveRepository: GameMoveRepository,
    private val gameRepository: GameRepository
) {
    fun findAllByGameUuid(gameUuid: UUID): List<GameMoveResponse> {
        /**
         * Get all moves by [gameUuid] order by id
         */
        return gameMoveRepository
            .findAllByGameUuidOrderById(gameUuid)
            .mapIndexed() { index, gameMove -> gameMove.toGameMoveResponse(index) }
    }

    fun create(newGameMoveRequest: NewGameMoveRequest) {
        /**
         * Creates a new move in the Game
         */
        try {
            val game = gameRepository.findByUuid(newGameMoveRequest.gameUuid)

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
