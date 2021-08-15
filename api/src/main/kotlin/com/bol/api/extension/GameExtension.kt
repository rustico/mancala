package com.bol.api.extension

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.dto.SimpleGameResponse
import com.bol.api.model.Game
import lib.MancalaGame

fun Game.toGameResponse(mancalaGame: MancalaGame) = GameResponse(
    uuid = uuid,
    createdAt = createdAt,
    updatedAt = updatedAt,
    playerOneBoard = mancalaGame.playerOneBoard,
    playerOneBank = mancalaGame.playerOneBank,
    playerTwoBoard = mancalaGame.playerTwoBoard,
    playerTwoBank = mancalaGame.playerTwoBank
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
