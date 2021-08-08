package com.bol.mancalarestapi.extension

import com.bol.mancalarestapi.dto.GameMoveResponse
import com.bol.mancalarestapi.model.GameMove

fun GameMove.toGameMoveResponse(index: Int) = GameMoveResponse (
    turn = index,
    move = move,
    createdAt = createdAt
)