package com.bol.api.controller

import com.bol.api.dto.GameResponse
import com.bol.api.dto.JoinGameRequest
import com.bol.api.dto.JoinGameResponse
import com.bol.api.dto.NewGameRequest
import com.bol.api.dto.NewGameResponse
import com.bol.api.dto.SimpleGameResponse
import com.bol.api.service.GameService
import org.springframework.messaging.simp.SimpMessagingTemplate
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
class GameController(private val gameService: GameService, private val template: SimpMessagingTemplate) {
    @GetMapping
    fun getGames(): List<SimpleGameResponse> {
        return gameService.findAll()
    }

    @PostMapping
    fun createGame(@RequestBody newGameRequest: NewGameRequest): NewGameResponse {
        return gameService.create(numberOfStones = newGameRequest.numberOfStones)
    }

    @GetMapping("/{uuid}")
    fun getGameByUuid(@PathVariable uuid: UUID): GameResponse {
        return gameService.findByUuid(uuid = uuid)
    }

    @PutMapping("/{uuid}")
    fun joinGame(
        @PathVariable uuid: UUID,
        @RequestBody joinGameRequest: JoinGameRequest
    ): JoinGameResponse {
        val joinGame = gameService.joinGame(gameUuid = uuid, invitationApiKey = joinGameRequest.invitationApiKey)
        template.convertAndSend("/topic/${joinGameRequest.invitationApiKey}", "holines")
        return joinGame
    }
}
