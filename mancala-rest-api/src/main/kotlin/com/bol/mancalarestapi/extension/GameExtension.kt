package com.bol.mancalarestapi.extension

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.model.Game

fun Game.toGameResponse() = GameResponse(
    key = key,
    playerOne = playerOne,
    playerTwo = playerTwo,
    createdAt = createdAt,
    updatedAt = updatedAt
)
