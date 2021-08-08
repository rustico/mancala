package com.bol.mancalarestapi.dto

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column

class NewGameRequest (
    val playerOne: String,
    val playerTwo: String?
)

class GameResponse (
    val key: UUID,
    val playerOne: String,
    val playerTwo: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)