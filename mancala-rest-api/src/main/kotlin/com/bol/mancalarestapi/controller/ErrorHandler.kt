package com.bol.mancalarestapi.controller

import com.bol.mancalarestapi.service.GameAlreadyStartedException
import com.bol.mancalarestapi.service.GameNotFoundException
import com.bol.mancalarestapi.service.InvalidAPIKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(GameAlreadyStartedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleGameAlreadyStarted(e: GameAlreadyStartedException): String = e.message!!

    @ExceptionHandler(InvalidAPIKeyException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidAPIKey(e: InvalidAPIKeyException): String = e.message!!

    @ExceptionHandler(GameNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleGameNotFound(e: GameNotFoundException): String = e.message!!
}