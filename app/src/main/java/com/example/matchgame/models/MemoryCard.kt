package com.example.matchgame.models

// Represents a single card in the memory game, holding its state and identifier
data class MemoryCard(
        val identifier: Int, // The resource ID of the card image
        var isFaceUp: Boolean = false, // Boolean indicating if the card is face up
        var isMatched: Boolean = false, // Boolean indicating if the card is matched
        var isBlocked: Boolean = false, // Boolean indicating if the card is blocked (used only in round 3)
        var availableReveals: Int = 2 // Number of times the player can reveal the card (used only in round 3)
)