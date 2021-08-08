package com.bol.mancalarestapi.service

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.JoinGameRequest
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import com.bol.mancalarestapi.extension.toGameResponse
import com.bol.mancalarestapi.extension.toNewGameResponse
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

    fun findByUuid(uuid: UUID): GameResponse {
        val game = gameRepository.findByUuid(uuid)
        return game.toGameResponse()
    }

    fun create(newGameRequest: NewGameRequest): NewGameResponse {
        val game = Game(
            playerOne = newGameRequest.playerOne,
        )
        return gameRepository.save(game).toNewGameResponse()
    }

    fun joinGame(gameUuid: UUID, apiKey: UUID, joinGameRequest: JoinGameRequest): NewGameResponse {
        val game = gameRepository.findByApiKeyAndUuid(apiKey, gameUuid)
        game.playerTwo = joinGameRequest.playerTwo
        gameRepository.save(game)
        return game.toNewGameResponse()
    }
}