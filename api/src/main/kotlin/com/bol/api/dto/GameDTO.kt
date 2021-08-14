package com.bol.api.dto

import java.time.LocalDateTime
import java.util.UUID


class GameResponse(
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
