package com.bol.api.service

import org.springframework.stereotype.Service
import com.bol.api.model.GameMove
import lib.MancalaGame
import org.springframework.dao.EmptyResultDataAccessException


@Service
class MancalaService() {
    fun getMancalaGame(numberOfStones: Int, gameMoves: Iterable<GameMove>) : MancalaGame {
        /**
         * Returns MancalaGame with its history of moves loaded
         */
        val mancalaGame = MancalaGame(numberOfStones = numberOfStones)
        for (gameMove in gameMoves) {
            mancalaGame.choosePitIndexAutoPlayer(gameMove.position)
        }

        return mancalaGame
    }
}