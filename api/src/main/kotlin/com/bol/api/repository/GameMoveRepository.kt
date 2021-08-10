package com.bol.api.repository

import com.bol.api.model.GameMove
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface GameMoveRepository : CrudRepository<GameMove, Int> {
    fun findAllByGameUuidOrderById(game: UUID): Iterable<GameMove>
}