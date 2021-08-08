package com.bol.mancalarestapi.repository

import com.bol.mancalarestapi.model.Game
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface GameRepository: CrudRepository<Game, Int> {
    fun findByKey(key: UUID): Game
}