package com.bol.api.dto

import java.time.LocalDateTime
import java.util.UUID

class NewGameMoveRequest(
    val position: Int,
    val apiKey: UUID,
    val gameUuid: UUID
)

class GameMoveResponse(
    val turn: Int,
    val position: Int,
    val createdAt: LocalDateTime?
)