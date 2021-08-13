package com.bol.api.extension

import com.bol.api.dto.GameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.model.Game

fun Game.toGameResponse() = GameResponse(
    uuid = uuid,
    playerOneName = playerOneName,
    playerTwoName = playerTwoName,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Game.toNewGameResponse() = NewGameResponse(
    uuid = uuid,
    playerOneName = playerOneName,
    playerOneApiKey = playerOneApiKey,
    playerTwoName = playerTwoName,
    playerTwoApiKey = playerTwoApiKey,
    createdAt = createdAt,
    updatedAt = updatedAt
)
