package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.dto.GameResponse
import com.bol.mancalarestapi.dto.JoinGameRequest
import com.bol.mancalarestapi.dto.NewGameRequest
import com.bol.mancalarestapi.dto.NewGameResponse
import com.bol.mancalarestapi.service.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping("/{uuid}")
    fun getGameByUuid(@PathVariable uuid: UUID): GameResponse {
        return gameService.findByUuid(uuid)
    }

    @PutMapping("/{uuid}/join/{apiKey}")
    fun joinGame(@PathVariable uuid: UUID,
                 @PathVariable apiKey: UUID,
                 @RequestBody joinGameRequest: JoinGameRequest): NewGameResponse {
        return gameService.joinGame(uuid, apiKey, joinGameRequest)
    }

    @PostMapping()
    fun postGame(@RequestBody newGameRequest: NewGameRequest): NewGameResponse {
        return gameService.create(newGameRequest)
    }
}