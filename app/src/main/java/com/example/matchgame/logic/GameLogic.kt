package com.example.matchgame.logic

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

//contains the game logic, handling the card flipping, checking for matches, and updating the game state
class GameLogic(
    private val buttons: List<ImageButton>,
    private val updateViewsCallback: (List<MemoryCard>) -> Unit // Callback per aggiornare le viste dopo le operazioni di gioco
) {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null // Indice della carta selezionata, se ce n'è solo una

    init {
        setupGame() // Inizializza il gioco
    }

    private fun setupGame() {
        val images = mutableListOf(
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four
        )

        images.addAll(images) // duplica gli elementi della lista "images" all'interno di se stessa.
        images.shuffle() // Randomize the order of image

        cards = buttons.indices.map { index ->
            MemoryCard(images[index])
        }  /* Crea le carte in base alle immagini e assegna a ciascuna una MemoryCard,
        stiamo mappando ogni indice della lista buttons ad un oggetto MemoryCard*/
        updateViews() // Chiama la callback per aggiornare le viste con lo stato aggiornato del gioco
    }

    fun onCardClicked(position: Int) {
        val card = cards[position]
        if (card.isFaceUp) { //se clicchiamo una carta già visibile, visualizzaiamo:"Invalid move"
            Toast.makeText(buttons[position].context, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }

        if (indexOfSingleSelectedCard == null) {
            restoreCards() //se non c'è nessuna carta selezionata, viene chiamoto il metodo restoreCards()
            indexOfSingleSelectedCard = position //viene salvato l'indice della carta appena selezionata
        } else {
            checkForMatch(indexOfSingleSelectedCard!!, position) //viene controllato se carta appena selezionata e quella già selezionata sono uguali
            indexOfSingleSelectedCard = null //viene rimosso l'indice della carta selezionata
        }
        card.isFaceUp = !card.isFaceUp  // Inverte lo stato della carta (girata o non girata)
        updateViews()
    }
    fun saveState(savedState: Bundle) {
        // Salva lo stato del gioco nel bundle
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
    }

    fun restoreState(savedInstanceState: Bundle) {
        // Ripristina lo stato del gioco dal bundle
        val identifiers = savedInstanceState.getIntArray("cardIdentifiers")
        val isFaceUp = savedInstanceState.getBooleanArray("isFaceUp")
        val isMatched = savedInstanceState.getBooleanArray("isMatched")

        if (identifiers != null && isFaceUp != null && isMatched != null) { //Verificando che tutti e tre gli array siano diversi da null, ci assicuriamo che i dati siano stati correttamente salvati e possiamo procedere con il ripristino dello stato delle carte.
            cards = identifiers.indices.map { index -> //riassociamo a ogni carta le relative informazioni di Memory Card
                MemoryCard(
                    identifier = identifiers[index],
                    isFaceUp = isFaceUp[index],
                    isMatched = isMatched[index]
                )
            }
        }
        indexOfSingleSelectedCard = savedInstanceState.getInt("indexOfSingleSelectedCard") .takeIf { it != -1 }
        /* se c'è una carta precedentemente selezionata, ne ripristiniamo l'indice, è cruciale inserire il metodo: ".takeIf { it != -1 }" ,
        poichè se non è stata selezionata nessuna carta in precedenza, allora: savedInstanceState.getInt("indexOfSingleSelectedCard") restituira : -1,
        e di conseguenza: indexOfSingleSelectedCard dovrà essere null
         */
        updateViews()
    }
    private fun restoreCards() { //riporta le carte non accoppiate allo stato originale (non girato)
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
