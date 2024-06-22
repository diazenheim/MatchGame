package com.example.matchgame.logic

import android.os.Bundle
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

class MultiplayerGameLogic(
    private val updateViewsCallback: (List<MemoryCard>) -> Unit,
    private val onAllCardsMatchedCallback: () -> Unit,
    private val ToastContextCallback: (String) -> Unit,
    private val numberOfCards: Int,
    private val currentPlayerProvider: () -> Int
) : IGameLogic {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null
    private var player1Score = 0
    private var player2Score = 0

    init {
        setupGame()
    }

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
        images.addAll(images)
        images.shuffle()

        val tempCards = mutableListOf<MemoryCard>()
        for (index in 0 until numberOfCards) {
            tempCards.add(MemoryCard(images[index]))
        }
        cards = tempCards
        updateViews()
    }

    override fun onCardClicked(position: Int) {
        val card = cards[position]
        if (card.isMatched || card.isFaceUp) return

        if (indexOfSingleSelectedCard == null) {
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        updateViews()
        checkAllMatched()
    }

    override fun saveState(savedState: Bundle) {
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
        savedState.putInt("player1Score", player1Score)
        savedState.putInt("player2Score", player2Score)
        savedState.putInt("currentPlayer", currentPlayerProvider.invoke())
    }

    override fun restoreState(savedInstanceState: Bundle) {
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
    }

    private fun restoreCards() {
        cards.forEach { if (!it.isMatched) it.isFaceUp = false }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1].identifier == cards[position2].identifier) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            ToastContextCallback("Match found!")
            if (currentPlayerProvider.invoke() == 1) {
                player1Score++
            } else {
                player2Score++
            }
        }
    }

    private fun updateViews() {
        updateViewsCallback(cards)
    }

    private fun checkAllMatched() {
        if (cards.all { it.isMatched }) {
            onAllCardsMatchedCallback()
        }
    }

    override fun getCards(): List<MemoryCard> = cards

    override fun determineWinner(): Int {
        return if (player1Score > player2Score) 1 else 2
    }
}
