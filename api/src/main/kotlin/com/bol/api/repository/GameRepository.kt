package com.bol.api.repository

import com.bol.api.model.Game
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface GameRepository: CrudRepository<Game, Int> {
    fun findByUuid(uuid: UUID): Game
    fun findByApiKeyAndUuid(apiKey: UUID, uuid: UUID): Game
}