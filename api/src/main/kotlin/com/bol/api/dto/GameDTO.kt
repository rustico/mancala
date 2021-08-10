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
    val apiKey: UUID,
    val playerOneName: String,
    val playerTwoName: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
