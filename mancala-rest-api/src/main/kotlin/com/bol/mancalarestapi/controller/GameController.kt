package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.service.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/games")
class GameController(private val gameService: GameService) {
    @GetMapping
    fun getGames(): List<GameResponse> {
        return gameService.findAll()
    }

    @GetMapping("/{key}")
    fun getGameByKey(@PathVariable key: UUID): GameResponse {
        return gameService.findByKey(key)
    }

    @PostMapping()
    fun postGame(@RequestBody newGameRequest: NewGameRequest): GameResponse {
        return gameService.create(newGameRequest)
    }
}