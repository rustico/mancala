package com.bol.api.dto

import java.time.LocalDateTime
import java.util.UUID

class NewGameRequest(
    val playerOneName: String,
)

class JoinGameRequest(
    val playerTwoName: String
)

class GameResponse(
    val uuid: UUID,
    val playerOneName: String,
    val playerTwoName: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

class NewGameResponse(
    val uuid: UUID,
    val playerOneName: String,
    val playerOneApiKey: UUID,
    val playerTwoName: String?,
    val playerTwoApiKey: UUID,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
