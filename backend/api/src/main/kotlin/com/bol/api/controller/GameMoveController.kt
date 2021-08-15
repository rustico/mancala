package com.bol.api.controller

import com.bol.api.dto.GameMoveResponse
import com.bol.api.dto.GameResponse
import com.bol.api.dto.NewGameMoveRequest
import com.bol.api.service.GameMoveService
import com.bol.api.service.MancalaService
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/gamesmoves")
class GameMoveController(
    private val gameMoveService: GameMoveService,
    private val mancalaService: MancalaService,
    private val template: SimpMessagingTemplate
) {
    @GetMapping("/{gameUuid}")
    fun getGameMoves(@PathVariable gameUuid: UUID): List<GameMoveResponse> {
        return gameMoveService.findAllByGameUuid(gameUuid)
    }

    @PostMapping
    fun postGameMove(@RequestBody newGameMoveRequest: NewGameMoveRequest): GameResponse {
        val mancalaGame = mancalaService.getMancalaGame(newGameMoveRequest.gameUuid)

        mancalaService.playMove(mancalaGame, newGameMoveRequest)

        val gameResponse = gameMoveService.create(mancalaGame, newGameMoveRequest)

        template.convertAndSend("/topic/${newGameMoveRequest.gameUuid}", gameResponse)

        return gameResponse
    }
}