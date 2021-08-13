package com.bol.api.service

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameRequest
import com.bol.api.dto.NewGameRequest
import com.bol.api.dto.NewGameResponse
import com.bol.api.extension.toGameResponse
import com.bol.api.extension.toNewGameResponse
import com.bol.api.model.Game
import com.bol.api.repository.GameRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val gameRepository: GameRepository) {
    fun findAll(): List<GameResponse> {
        /**
         * Returns all gamess played
         */
        return gameRepository
            .findAll()
            .map { it.toGameResponse() }
    }

    fun findByUuid(uuid: UUID): GameResponse {
        /**
         * Returns game by its [uuid]
         */
        try {
            val game = gameRepository.findByUuid(uuid)
            return game.toGameResponse()

        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }

    fun create(newGameRequest: NewGameRequest): NewGameResponse {
        /**
         * Game is created by PlayerOne.
         *
         * Returns a [NewGameResponse] with the API keys to play the game and invite another player
         */
        val game = Game(
            playerOneName = newGameRequest.playerOneName,
        )
        return gameRepository.save(game).toNewGameResponse()
    }

    fun joinGame(gameUuid: UUID, playerApiKey: UUID, joinGameRequest: JoinGameRequest): NewGameResponse {
        /**
         * PlayerTwo joins the game using his API key [playerApiKey] sent in the invitation link
         */
        try {
            val game = gameRepository.findByUuid(gameUuid)

            if (game.playerTwoName != null) {
                throw GameAlreadyStartedException()
            }
            if (game.playerTwoApiKey != playerApiKey) {
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