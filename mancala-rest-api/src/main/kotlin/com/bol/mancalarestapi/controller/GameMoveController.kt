package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameMoveResponse
import com.bol.mancalarestapi.dto.NewGameMoveRequest
import com.bol.mancalarestapi.service.GameMoveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/gamesmoves")
class GameMoveController(private val gameMoveService: GameMoveService) {
    @GetMapping("/{gameUuid}")
    fun getGameMoves(@PathVariable gameUuid: UUID): List<GameMoveResponse> {
        return gameMoveService.findAllByGameUuid(gameUuid)
    }

    @PostMapping
    fun postGameMove(@RequestBody newGameMoveRequest: NewGameMoveRequest) {
        gameMoveService.create(newGameMoveRequest)
    }
}