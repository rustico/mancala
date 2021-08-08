package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameMoveResponse
import com.bol.mancalarestapi.dto.GetGameMovesRequest
import com.bol.mancalarestapi.dto.NewGameMoveRequest
import com.bol.mancalarestapi.service.GameMoveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gamesmoves")
class GameMoveController (private val gameMoveService: GameMoveService) {
    @GetMapping
    fun getGameMoves(@RequestBody getGameMovesRequest: GetGameMovesRequest): List<GameMoveResponse> {
        return gameMoveService.findAllByGameKey(getGameMovesRequest)
    }

    @PostMapping
    fun postGameMove(@RequestBody newGameMoveRequest: NewGameMoveRequest) {
        gameMoveService.create(newGameMoveRequest)
    }
}