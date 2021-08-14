package com.bol.api.service

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.extension.toGameResponse
import com.bol.api.extension.toJoinGameResponse
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

    fun create(): NewGameResponse {
        /**
         * Game is created by PlayerOne.
         *
         * Returns a [NewGameResponse] with the API keys to play the game and invite another player
         */
        return gameRepository.save(Game()).toNewGameResponse()
    }

    fun joinGame(gameUuid: UUID, invitationApiKey: UUID): JoinGameResponse {
        /**
         * PlayerTwo joins the game using his API key [invitationApiKey] sent in the invitation link
         */
        try {
            val game = gameRepository.findByUuid(gameUuid)

            if (game.invitationApiKey == null) {
                throw GameAlreadyStartedException()
            }
            if (game.invitationApiKey != invitationApiKey) {
                throw InvalidAPIKeyException()
            }

            game.invitationApiKey = null
            gameRepository.save(game)

            return game.toJoinGameResponse()

        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}