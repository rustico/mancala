package com.bol.api.service

import org.springframework.stereotype.Service
import com.bol.api.dto.MancalaGameResponse
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.repository.GameMoveRepository
import lib.MancalaGame

@Service
class MancalaService(private val gameMoveRepository: GameMoveRepository) {
    fun getMancalaGame(newGameMoveRequest: NewGameMoveRequest) : MancalaGame {
        val gameMoves = gameMoveRepository.findAllByGameUuidOrderById(newGameMoveRequest.gameUuid)
        val mancalaGame = MancalaGame()
        for (gameMove in gameMoves) {
            mancalaGame.choosePitIndexAutoPlayer(gameMove.position)
        }

        return mancalaGame
    }

    fun playMove(mancalaGame: MancalaGame, newGameMoveRequest: NewGameMoveRequest): MancalaGameResponse {
        mancalaGame.choosePitIndexAutoPlayer(newGameMoveRequest.position)

        return MancalaGameResponse(
            playerOneBoard = mancalaGame.playerOneBoard,
            playerOneBank = mancalaGame.playerOneBank,
            playerTwoBoard = mancalaGame.playerTwoBoard,
            playerTwoBank = mancalaGame.playerTwoBank
        )
    }
}