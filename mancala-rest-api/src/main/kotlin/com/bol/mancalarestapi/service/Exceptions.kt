package com.bol.mancalarestapi.service

class GameAlreadyStartedException : Exception("Game already has two players")
class InvalidAPIKeyException : Exception("Invalid API Key")
class GameNotFoundException : Exception("Game not found")
