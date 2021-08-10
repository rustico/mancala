package com.bol.mancalarestapi.service

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.JoinGameRequest
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import com.bol.mancalarestapi.extension.toGameResponse
import com.bol.mancalarestapi.extension.toNewGameResponse
import com.bol.mancalarestapi.model.Game
import com.bol.mancalarestapi.repository.GameRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val gameRepository: GameRepository) {
    fun findAll(): List<GameResponse> {
        return gameRepository
            .findAll()
            .map { it.toGameResponse() }
    }

    fun findByUuid(uuid: UUID): GameResponse {
        try {
            val game = gameRepository.findByUuid(uuid)
            return game.toGameResponse()

        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }

    fun create(newGameRequest: NewGameRequest): NewGameResponse {
        val game = Game(
            playerOneName = newGameRequest.playerOneName,
        )
        return gameRepository.save(game).toNewGameResponse()
    }

    fun joinGame(gameUuid: UUID, apiKey: UUID, joinGameRequest: JoinGameRequest): NewGameResponse {
        try {
            val game = gameRepository.findByUuid(gameUuid)

            if (game.playerTwoName != null) {
                throw GameAlreadyStartedException()
            }
            if (game.apiKey != apiKey) {
                throw InvalidAPIKeyException()
            }

            game.playerTwoName = joinGameRequest.playerTwoName
            gameRepository.save(game)

            return game.toNewGameResponse()

        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}