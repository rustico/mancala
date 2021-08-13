package com.bol.api.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.Table

@Entity
@Table(name = "games")
class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    @Column(nullable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val playerOneName: String,

    @Column(nullable = false)
    val playerOneApiKey: UUID = UUID.randomUUID(),

    var playerTwoName: String? = null,

    @Column(nullable = false)
    val playerTwoApiKey: UUID = UUID.randomUUID(),

    var winner: Int? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null
)