package com.bol.api.service

import org.springframework.stereotype.Service
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.model.GameMove
import com.bol.api.repository.GameMoveRepository
import com.bol.api.repository.GameRepository
import lib.MancalaGame
import lib.MancalaPlayer
import org.springframework.dao.EmptyResultDataAccessException


@Service
class MancalaService() {
    fun getMancalaGame(numberOfStones: Int, gameMoves: Iterable<GameMove>) : MancalaGame {
        /**
         * Returns MancalaGame with its history of moves loaded
         */
        try {
            val mancalaGame = MancalaGame(numberOfStones = numberOfStones)
            for (gameMove in gameMoves) {
                mancalaGame.choosePitIndexAutoPlayer(gameMove.position)
            }

            return mancalaGame
        } catch (e: EmptyResultDataAccessException) {
            throw GameNotFoundException()
        }
    }
}