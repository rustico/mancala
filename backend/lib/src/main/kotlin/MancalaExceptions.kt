package lib

class InvalidPlayerTurnException : Exception("Is not players turn")
class EmptyPitException : Exception("Cannot choose an empty pit")
class InvalidPitIndexException : Exception("Pit index needs to be between 1 and 6")