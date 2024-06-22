package com.example.matchgame.logic

import android.os.Bundle
import android.util.Log
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

class GameLogic(

    private val updateViewsCallback: (List<MemoryCard>) -> Unit, //non gestiamo più la visualizzazione delle carte con una lista di image Button bensì affidiamo la presentazione a CardAdapter, in modo da separare la logica del gioco dalla logica di presentazione
    private val onAllCardsMatchedCallback: () -> Unit, //questa callback controlla se tutte le carte sono state matchate
    private val ToastContextCallback: (String) -> Unit,// Questa callback permette di mostrare il Toast nel fragment che ha inizializzato l'istanza di gameLogic
    private var round: Int, //questa variabile traccia il round corrente
    private val numberOfCards: Int, //identifica il numero di carte
    private val currentPlayerProvider: (() -> Int)? = null // Optional provider for multiplayer mode
) {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null

    private var player1Score = 0
    private var player2Score = 0


    init {
        setupGame()
    }

    private fun setupGame() { //a seconda che il round sia 1 o 2, la lista di immagini cambia
        val images: MutableList<Int> = mutableListOf()
        if (round == 1) {
            images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four
            ))
        } else if (round == 2){
            images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,
            ))
        } else if (round == 3) {
            images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,
                R.drawable.seven,
                R.drawable.eight
            ))
        }
        else if (round == 0){
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
                R.drawable.eleven
            ))
        }

        images.addAll(images)// Raddoppio delle immagini per creare coppie
        images.shuffle() //mischio carte
        val tempCards = mutableListOf<MemoryCard>() //creo una lista temporanea per gestire l'inserimento delle carte
        for (index in 0 until (numberOfCards)) {
            tempCards.add(MemoryCard(images[index]))
        }
        cards = tempCards

        updateViews()
    }

    fun onCardClicked(position: Int) {
        val card = cards[position]
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
        checkAllMatched()
    }

    //?????
    fun saveState(savedState: Bundle) { //salviamo lo stato in un Bundle
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1) //inseriamo -1 se indexOfSingleSelectedCard è null
        savedState.putInt("player1Score", player1Score)
        savedState.putInt("player2Score", player2Score)
        savedState.putInt("currentPlayer", currentPlayerProvider?.invoke() ?: 1) // Save current player only if in multiplayer
    }

    fun restoreState(savedInstanceState: Bundle) {
        val identifiers = savedInstanceState.getIntArray("cardIdentifiers")
        val isFaceUp = savedInstanceState.getBooleanArray("isFaceUp")
        val isMatched = savedInstanceState.getBooleanArray("isMatched")

        if (identifiers != null && isFaceUp != null && isMatched != null) { //questo if controlla che ci sia un Bundle salvato correttamente
            cards = identifiers.indices.map { index ->
                MemoryCard(
                    identifier = identifiers[index],
                    isFaceUp = isFaceUp[index],
                    isMatched = isMatched[index]
                )
            }
        }
        indexOfSingleSelectedCard = savedInstanceState.getInt("indexOfSingleSelectedCard").takeIf { it != -1 } //se troviamo un valore uguale a -1, significa che non c'era nessuna carta selezionata
        player1Score = savedInstanceState.getInt("player1Score")
        player2Score = savedInstanceState.getInt("player2Score")
        if (currentPlayerProvider != null) {
            currentPlayerProvider.invoke() // Restore current player only if in multiplayer
        }
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
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            ToastContextCallback("Match found!")
            if (currentPlayerProvider?.invoke() == 1) {
                player1Score++
            } else {
                player2Score++
            }
        }
    }

    private fun updateViews() {
        updateViewsCallback(cards)
    }
    private fun checkAllMatched() { //questo metodo controlla se tutte le carte sono state matchate ed in caso affermativo ritorna la callback
        var allMatched = true
        for (card in cards) {
            if (!card.isMatched) {
                allMatched = false
                break
            }
        }
        if (allMatched) {
            Log.d("GameLogic", "All cards matched, invoking callback")
            onAllCardsMatchedCallback()
        }
    }

    fun getCards(): List<MemoryCard> {
        return cards
    }

    fun determineWinner(): Int {
        return if (player1Score > player2Score) 1 else 2
    }
}

