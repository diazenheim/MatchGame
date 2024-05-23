package com.example.matchgame.logic

import android.widget.ImageButton
import android.widget.Toast
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

//contains the game logic, handling the card flipping, checking for matches, and updating the game state
class GameLogic(
    private val buttons: List<ImageButton>,
    private val updateViewsCallback: (List<MemoryCard>) -> Unit
) {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null

    init {
        setupGame()
    }

    private fun setupGame() {
        val images = mutableListOf(
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four
        )
        // Add each image twice so that pairs can be created
        images.addAll(images)
        // Randomize the order of images
        images.shuffle()

        cards = buttons.indices.map { index ->
            MemoryCard(images[index])
        }
        updateViews()
    }

    fun onCardClicked(position: Int) {
        val card = cards[position]
        if (card.isFaceUp) {
            Toast.makeText(buttons[position].context, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }

        if (indexOfSingleSelectedCard == null) {
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        updateViews()
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1].identifier == cards[position2].identifier) {
            Toast.makeText(buttons[position1].context, "Match found!!", Toast.LENGTH_SHORT).show()
            cards[position1].isMatched = true
            cards[position2].isMatched = true
        }
    }

    private fun updateViews() {
        updateViewsCallback(cards)
    }
}
