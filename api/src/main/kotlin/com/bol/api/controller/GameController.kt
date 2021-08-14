package com.bol.api.controller

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameResponse
import com.bol.api.service.GameService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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

    @RequestMapping("/new")
    fun createGame(): NewGameResponse {
        return gameService.create()
    }

    @GetMapping("/{uuid}")
    fun getGameByUuid(@PathVariable uuid: UUID): GameResponse {
        return gameService.findByUuid(uuid)
    }

    @GetMapping("/{uuid}/join/{invitationApiKey}")
    fun joinGame(
        @PathVariable uuid: UUID,
        @PathVariable invitationApiKey: UUID,
    ): JoinGameResponse {
        return gameService.joinGame(uuid, invitationApiKey)
    }
}