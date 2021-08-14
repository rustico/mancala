package com.bol.api.controller

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameRequest
import com.bol.api.dto.NewGameRequest
import com.bol.api.dto.NewGameResponse
import com.bol.api.service.GameService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
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

    @PutMapping("/{uuid}/join/{playerTwoApiKey}")
    fun joinGame(
        @PathVariable uuid: UUID,
        @PathVariable playerTwoApiKey: UUID,
        @RequestBody joinGameRequest: JoinGameRequest
    ): NewGameResponse {
        return gameService.joinGame(uuid, playerTwoApiKey, joinGameRequest)
    }

    @PostMapping()
    fun postGame(@RequestBody newGameRequest: NewGameRequest): NewGameResponse {
        return gameService.create(newGameRequest)
    }
}