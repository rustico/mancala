package com.bol.api.extension

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.dto.SimpleGameResponse
import com.bol.api.model.Game
import com.bol.api.utils.shortUuid
import lib.MancalaGame
import lib.MancalaPlayer

fun Game.toGameResponse(mancalaGame: MancalaGame) = GameResponse(
    uuid = uuid,
    createdAt = createdAt,
    updatedAt = updatedAt,
    playerOneBoard = mancalaGame.playerOneBoard,
    playerOneBank = mancalaGame.playerOneBank,
    playerOneId = shortUuid(playerOneApiKey),
    playerTwoBoard = mancalaGame.playerTwoBoard,
    playerTwoBank = mancalaGame.playerTwoBank,
    playerTwoId = shortUuid(playerTwoApiKey),
    playerTurn = if (mancalaGame.playerTurn == MancalaPlayer.PlayerOne) shortUuid(playerOneApiKey) else shortUuid(playerTwoApiKey),
    winner = winner
)

fun Game.toSimpleGameResponse() = SimpleGameResponse(
    uuid = uuid,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Game.toNewGameResponse() = NewGameResponse(
    uuid = uuid,
    apiKey = playerOneApiKey,
    invitationApiKey = invitationApiKey!!,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Game.toJoinGameResponse() = JoinGameResponse(
    uuid = uuid,
    apiKey = playerTwoApiKey,
    createdAt = createdAt,
    updatedAt = updatedAt
)
