package com.example.matchgame.logic

import android.os.Bundle
import com.example.matchgame.models.MemoryCard

// Interface representing the game logic for the matching cards game
interface IGameLogic {

    // Handles the event when a card is clicked
    fun onCardClicked(position: Int)

    // Saves the current state of the game
    fun saveState(savedState: Bundle)

    // Restores the game state from a previously saved state
    fun restoreState(savedInstanceState: Bundle)

    // Returns the list of memory cards currently being used in the game
    fun getCards(): List<MemoryCard>

    // Determines the winner of the game
    fun determineWinner(): Int?

    // Gets the score for a specific player
    fun getScore(player: String): Int
}
