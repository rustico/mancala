package com.bol.mancalarestapi.dto

import java.time.LocalDateTime
import java.util.UUID

class NewGameRequest (
    val playerOne: String,
)

class JoinGameRequest (
    val playerTwo: String
)

class GameResponse (
    val uuid: UUID,
    val playerOne: String,
    val playerTwo: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

class NewGameResponse (
    val uuid: UUID,
    val apiKey: UUID,
    val playerOne: String,
    val playerTwo: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
