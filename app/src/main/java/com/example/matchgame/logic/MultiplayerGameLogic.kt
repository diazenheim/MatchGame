package com.example.matchgame.logic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MultiplayerGameLogic(
    private val updateViewsCallback: (List<MemoryCard>) -> Unit, // Callback to update the view with the current cards
    private val onAllCardsMatchedCallback: () -> Unit, // Callback when all cards are matched
    private val toastContextCallback: (String) -> Unit, // Callback to show Toast messages
    private val numberOfCards: Int, // Number of cards in the game
    private val currentPlayerProvider: () -> Int, // Provides the current player
    private val switchPlayerCallback: () -> Unit, // Callback to switch the player
    ) : IGameLogic {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null
    private var player1Score = 0
    private var player2Score = 0
    private val handler = Handler(Looper.getMainLooper())
    private var score=0


    init {
        try {
            setupGame()
        }catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error during game setup", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Setup the game with cards
    private fun setupGame() {
        val images: MutableList<Int> = mutableListOf()
        images.addAll(listOf(
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four,
            R.drawable.five,
            R.drawable.six,
            R.drawable.seven,
            R.drawable.eight,
            R.drawable.nine,
            R.drawable.ten,
            R.drawable.eleven))

        images.addAll(images) // Duplicate the images
        images.shuffle()

        // Create MemoryCard objects for each image
        val tempCards = mutableListOf<MemoryCard>()
        for (index in 0 until numberOfCards) {
            tempCards.add(MemoryCard(images[index]))
        }
        cards = tempCards
        updateViews()
    }

    // Handle card click events
    override fun onCardClicked(position: Int) {
        try {
            val card = cards[position]
            if (card.isMatched || card.isFaceUp) return

            indexOfSingleSelectedCard = if (indexOfSingleSelectedCard == null) {
                restoreCards()
                position
            } else {
                checkForMatch(indexOfSingleSelectedCard!!, position)
                null
            }
            card.isFaceUp = !card.isFaceUp
            updateViews()
            checkAllMatched()
        }catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error during card click handling", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Save the current game state
    override fun saveState(savedState: Bundle) {
        try {
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
        savedState.putInt("player1Score", player1Score)
        savedState.putInt("player2Score", player2Score)
        savedState.putInt("currentPlayer", currentPlayerProvider.invoke())
        } catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error saving game state", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Restore the game state from a saved state
    override fun restoreState(savedInstanceState: Bundle) {
        try{
        val identifiers = savedInstanceState.getIntArray("cardIdentifiers")
        val isFaceUp = savedInstanceState.getBooleanArray("isFaceUp")
        val isMatched = savedInstanceState.getBooleanArray("isMatched")

        if (identifiers != null && isFaceUp != null && isMatched != null) {
            cards = identifiers.indices.map { index ->
                MemoryCard(identifiers[index], isFaceUp[index], isMatched[index])
            }
        }
        indexOfSingleSelectedCard = savedInstanceState.getInt("indexOfSingleSelectedCard").takeIf { it != -1 }
        player1Score = savedInstanceState.getInt("player1Score")
        player2Score = savedInstanceState.getInt("player2Score")
        currentPlayerProvider.invoke()
        updateViews()
        } catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error restoring game state", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Restore the cards' state
    private fun restoreCards() {
        try {
        cards.forEach { if (!it.isMatched) it.isFaceUp = false }
        } catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error restoring cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Check if two cards match
    private fun checkForMatch(position1: Int, position2: Int) {
        try {
        if (cards[position1].identifier == cards[position2].identifier) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            toastContextCallback("Good job!")
            if (currentPlayerProvider.invoke() == 1) {
                player1Score++
            } else {
                player2Score++
            }
        } else {
            handler.postDelayed({
                cards[position1].isFaceUp = false
                cards[position2].isFaceUp = false
                updateViews()
                switchPlayerCallback()
            }, 350)
        }
        } catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error checking for match", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Update the views with the current state of the cards
    private fun updateViews() {
        try {
            updateViewsCallback(cards)
        } catch (e:Exception){
            Log.e("MultiplayerGameLogic", "Error updating views", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Check if all cards are matched
    private fun checkAllMatched() {
        try {
        if (cards.all { it.isMatched }) {
            onAllCardsMatchedCallback()
        }
        } catch (e:Exception){
            Log.e("MultiplayerGameLogic", "Error checking if all cards are matched", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Get the current list of cards
    override fun getCards(): List<MemoryCard> {

     return try {
         cards
        } catch(e:Exception){
         Log.e("MultiplayerGameLogic", "Error getting cards", e)
         FirebaseCrashlytics.getInstance().recordException(e)
         emptyList()
     }
    }

    // Determine the winner
    override fun determineWinner(): Int {
        return try {
            return if (player1Score > player2Score) 1 else 2
        } catch(e:Exception) {
            Log.e("MultiplayerGameLogic", "Error determining winner", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            -1
        }
    }

    // Get the score for a player
    override fun getScore(player: String): Int {
        return try{
            score = if(player=="1") {
                player1Score
            } else
                player2Score
            return score
        } catch(e:Exception){
            Log.e("MultiplayerGameLogic", "Error getting score for player $player", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }
}
