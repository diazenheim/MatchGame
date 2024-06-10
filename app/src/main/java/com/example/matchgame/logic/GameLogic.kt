package com.example.matchgame.logic

import android.os.Bundle
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.telemetry.DataCollector

class GameLogic(

    private val updateViewsCallback: (List<MemoryCard>) -> Unit, //non gestiamo più la visualizzazione delle carte con una lista di image Button bensì affidiamo la presentazione a CardAdapter, in modo da separare la logica del gioco dalla logica di presentazione
    private val onAllCardsMatchedCallback: () -> Unit, //questa callback controlla se tutte le carte sono state matchate
    private val ToastContextCallback: (String) -> Unit,// Questa callback permette di mostrare il Toast nel fragment che ha inizializzato l'istanza di gameLogic
    private val round: Int //questa variabile traccia il round corrente

) {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null


    init {
        setupGame()
    }

     fun setupGame() { //a seconda che il round sia 1 o 2, la lista di immagini cambia
        val images: MutableList<Int> = mutableListOf()
        if (round == 1) {
            images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four
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
        } else if (round == 2){
            images.addAll(listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,
            ))
        }
        images.addAll(images)// Raddoppio delle immagini per creare coppie
        images.shuffle() //mischio carte
        val tempCards = mutableListOf<MemoryCard>() //creo una lista temporanea per gestire l'inserimento delle carte
        for (index in images.indices) {
            tempCards.add(MemoryCard(images[index]))
        }
        cards = tempCards

        updateViews()
    }

    fun onCardClicked(position: Int) {
        val card = cards[position]
        if (card.isMatched) {
            // Se la carta è già abbinata, non fare nulla
            return
        }
        if (card.isFaceUp) {
            // Se la carta è già scoperta, premendola viene rigirata
            card.isFaceUp = false
            if (indexOfSingleSelectedCard == position) { //una carta coperta non è più una carta selezionata
                indexOfSingleSelectedCard = null
            }
            updateViews()
            return
        }

        if (indexOfSingleSelectedCard == null) {
            restoreCards() //giriamo le carte non selezionate
            indexOfSingleSelectedCard = position
        } else {
            checkForMatch(indexOfSingleSelectedCard!!, position) //controlliamo se le carte sono uguali
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp //da carta scoperta a carta coperta, e viceversa
        updateViews()
        checkAllMatched()
    }

    fun saveState(savedState: Bundle) { //salviamo lo stato in un Bundle
        savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
        savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
        savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
        savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1) //inseriamo -1 se indexOfSingleSelectedCard è null
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
            onAllCardsMatchedCallback()
        }
    }
    fun getCards(): List<MemoryCard> = cards
}
