package com.example.matchgame.models

//represents a single card in the memory game, holding its state and identifier
data class MemoryCard(
        val identifier: Int,  //The resource ID of the card image
        var isFaceUp: Boolean = false,  //Boolean indicating if the card is face up
        var isMatched: Boolean = false  //Boolean indicating if the card is matched
)