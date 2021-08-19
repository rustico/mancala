package com.bol.api.repository

import com.bol.api.model.Game
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID
import javax.persistence.LockModeType

interface GameRepository: CrudRepository<Game, Int> {
    fun findByUuid(uuid: UUID): Game

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Game g WHERE g.uuid = ?1")
    fun findByUuidPessimistic(uuid: UUID): Game
}