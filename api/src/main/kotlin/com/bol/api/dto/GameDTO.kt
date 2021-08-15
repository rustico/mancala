package com.bol.api.dto

import java.time.LocalDateTime
import java.util.UUID


class GameResponse(
    val uuid: UUID,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val playerOneBoard: MutableList<Int>,
    val playerOneBank: Int,
    val playerOneId: String,
    val playerTwoBoard: MutableList<Int>,
    val playerTwoBank: Int,
    val playerTwoId: String,
    val playerTurn: String
)

class SimpleGameResponse(
    val uuid: UUID,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

class NewGameResponse(
    val uuid: UUID,
    val apiKey: UUID,
    val invitationApiKey: UUID,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

class JoinGameResponse(
    val uuid: UUID,
    val apiKey: UUID,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
