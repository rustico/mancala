package lib


enum class MancalaPlayer(val index: Int) {
    /** Maps Players pit in the board
     *
     * @param index indicates the first element of the Player board
     */
    PlayerOne(0),
    PlayerTwo(7)
}