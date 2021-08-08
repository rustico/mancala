package com.bol.mancalarestapi.dto

import java.time.LocalDateTime
import java.util.UUID

class GetGameMovesRequest (
    val gameKey: UUID
)

class NewGameMoveRequest (
    val move: Int,
    val apiKey: UUID,
    val uuid: UUID
)

class GameMoveResponse (
    val turn: Int,
    val move: Int,
    val createdAt: LocalDateTime?
)