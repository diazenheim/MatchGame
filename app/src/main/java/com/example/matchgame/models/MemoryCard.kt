package com.example.matchgame.models

//represents a single card in the memory game, holding its state and identifier
data class MemoryCard(
        val identifier: Int,  //The resource ID of the card image
        var isFaceUp: Boolean = false,  //Boolean indicating if the card is face up
        var isMatched: Boolean = false,  //Boolean indicating if the card is matched
        var isBlocked: Boolean = false, //questa property permette l'interazione o meno con una carta, verrà usata solo nel round 3
        var AvailableReveals: Int = 2 // Numero di volte che il giocatore può rivelare la carta, questa property verrà usata solo nel round 3
)