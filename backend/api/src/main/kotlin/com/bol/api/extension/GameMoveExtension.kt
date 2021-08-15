package com.bol.api.extension

import com.bol.api.dto.GameMoveResponse
import com.bol.api.model.GameMove

fun GameMove.toGameMoveResponse(index: Int) = GameMoveResponse(
    turn = index,
    position = position,
    createdAt = createdAt
)