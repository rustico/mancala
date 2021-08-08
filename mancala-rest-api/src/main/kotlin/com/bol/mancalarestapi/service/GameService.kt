package com.bol.mancalarestapi.service

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.extension.toGameResponse
import com.bol.mancalarestapi.model.Game
import com.bol.mancalarestapi.repository.GameRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val gameRepository: GameRepository) {
    fun findAll(): List<GameResponse> {
        return gameRepository
            .findAll()
            .map{ it.toGameResponse() }
    }

    fun findByKey(key: UUID): GameResponse {
        val game = gameRepository.findByKey(key)
        return game.toGameResponse()
    }

    fun create(newGameRequest: NewGameRequest): GameResponse {
        val game = Game(
            key = UUID.randomUUID(),
            playerOne = newGameRequest.playerOne,
            playerTwo = newGameRequest.playerTwo,
        )
        return gameRepository.save(game).toGameResponse()
    }
}