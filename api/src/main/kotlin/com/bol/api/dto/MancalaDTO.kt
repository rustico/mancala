package com.bol.api.dto

class MancalaGameResponse(
    val playerOneBoard: MutableList<Int>,
    val playerOneBank: Int,
    val playerTwoBoard: MutableList<Int>,
    val playerTwoBank: Int
)