package com.bol.api.service

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.dto.SimpleGameResponse
import com.bol.api.extension.toGameResponse
import com.bol.api.extension.toJoinGameResponse
import com.bol.api.extension.toNewGameResponse
import com.bol.api.extension.toSimpleGameResponse
import com.bol.api.model.Game
import com.bol.api.repository.GameRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(private val gameRepository: GameRepository, private val mancalaService: MancalaService) {
    fun findAll(): List<SimpleGameResponse> {
        /**
         * Returns all games played
         */
        return gameRepository
            .findAll()
            .map { it.toSimpleGameResponse() }
    }

    fun findByUuid(uuid: UUID): GameResponse {
        /**
         * Returns game by its [uuid]
         */
        try {
            val game = gameRepository.findByUuid(uuid)
            val mancala = mancalaService.getMancalaGame(uuid)
            return game.toGameResponse(mancala)

        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }

    fun create(numberOfStones: Int): NewGameResponse {
        /**
         * Game is created by PlayerOne.
         *
         * Returns a [NewGameResponse] with the API keys to play the game and invite another player
         */
        return gameRepository.save(Game(numberOfStones=numberOfStones)).toNewGameResponse()
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