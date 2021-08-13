package lib

class InvalidPlayerTurn : Exception("Is not players turn")
class EmptyPit : Exception("Cannot choose an empty pit")
class InvalidPitIndex : Exception("Pit index needs to be between 1 and 6")