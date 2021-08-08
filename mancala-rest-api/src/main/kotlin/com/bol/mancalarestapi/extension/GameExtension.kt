package com.bol.mancalarestapi.extension

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.NewGameResponse
import com.bol.mancalarestapi.model.Game

fun Game.toGameResponse() = GameResponse(
    uuid = uuid,
    playerOne = playerOne,
    playerTwo = playerTwo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Game.toNewGameResponse() = NewGameResponse(
    uuid = uuid,
    apiKey = apiKey,
    playerOne = playerOne,
    playerTwo = playerTwo,
    createdAt = createdAt,
    updatedAt = updatedAt
)
