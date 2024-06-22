package com.example.matchgame.logic

import android.os.Bundle
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

class SingleGameLogic(

    private val updateViewsCallback: (List<MemoryCard>) -> Unit, //non gestiamo più la visualizzazione delle carte con una lista di image Button bensì affidiamo la presentazione a CardAdapter, in modo da separare la logica del gioco dalla logica di presentazione
    private val onAllCardsMatchedCallback: () -> Unit, //questa callback controlla se tutte le carte sono state matchate
    private val ToastContextCallback: (String) -> Unit,// Questa callback permette di mostrare il Toast nel fragment che ha inizializzato l'istanza di gameLogic
    private var round: Int, //questa variabile traccia il round corrente
    private val numberOfCards: Int, //identifica il numero di carte
) : IGameLogic {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null

    init {
        setupGame()
    }

    private fun setupGame() {
        val images: MutableList<Int> = mutableListOf()
        when (round) {
            1 -> images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four))
            2 -> images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six))
            3 -> images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,
                R.drawable.seven,
                R.drawable.eight))
        }
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

        //WITHOUT THE POSSIBILITY TO FLIP THE CARDS BACK DOWN, TO BE DISCUSSED.

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


        //WITH THE POSSIBILITY TO FLIP THE CARDS BACK DOWN, TO BE DISCUSSED.

        /*val card = cards[position]
        if (card.isMatched) {
            // If the card is already matched, do nothing
            return
        }
        if (card.isFaceUp) {
            // If the card is already face up, turn it face down
            card.isFaceUp = false
            if (indexOfSingleSelectedCard == position) { // A turned down card is no longer selected
                indexOfSingleSelectedCard = null
            }
            updateViews()
            return
        }

        if (indexOfSingleSelectedCard == null) {
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            checkForMatch(indexOfSingleSelectedCard!!, position) // Check if cards are the same
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp // Toggle the card face up/down state
        updateViews()
        checkAllMatched()*/
    }

    override fun saveState(savedState: Bundle) {
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
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

    override fun determineWinner(): Int? {
        return null // Single player game does not determine a winner
    }
}
