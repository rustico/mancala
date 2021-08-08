package com.bol.mancalarestapi.service

import com.bol.mancalarestapi.dto.GameMoveResponse
import com.bol.mancalarestapi.dto.GetGameMovesRequest
import com.bol.mancalarestapi.dto.NewGameMoveRequest
import com.bol.mancalarestapi.extension.toGameMoveResponse
import com.bol.mancalarestapi.model.GameMove
import com.bol.mancalarestapi.repository.GameMoveRepository
import com.bol.mancalarestapi.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameMoveService(private val gameMoveRepository: GameMoveRepository,
                      private val gameRepository: GameRepository) {

    fun findAllByGameKey(getGameMovesRequest: GetGameMovesRequest): List<GameMoveResponse> {
        return gameMoveRepository
            .findAllByGameApiKeyOrderById(getGameMovesRequest.gameKey)
            .mapIndexed() { index, gameMove -> gameMove.toGameMoveResponse(index) }
    }

    fun create(newGameMoveRequest: NewGameMoveRequest) {
        val game = gameRepository.findByApiKeyAndUuid(newGameMoveRequest.apiKey, newGameMoveRequest.uuid)
        val gameMove = GameMove(
            game = game,
            move = newGameMoveRequest.move
        )

        gameMoveRepository.save(gameMove)
    }
}
