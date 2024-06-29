package com.example.matchgame.logic

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SingleGameLogic(

    private val updateViewsCallback: (List<MemoryCard>) -> Unit, //non gestiamo più la visualizzazione delle carte con una lista di image Button bensì affidiamo la presentazione a CardAdapter, in modo da separare la logica del gioco dalla logica di presentazione
    private val onAllCardsMatchedCallback: () -> Unit, //questa callback controlla se tutte le carte sono state matchate
    private val ToastContextCallback: (String) -> Unit,// Questa callback permette di mostrare il Toast nel fragment che ha inizializzato l'istanza di gameLogic
    private var round: Int, //questa variabile traccia il round corrente
    private val numberOfCards: Int, //identifica il numero di carte

) : IGameLogic {

    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null
    private var handler = Handler(Looper.getMainLooper())

    init {
        try {


            setupGame()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error during game setup", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setupGame() {
        val images: MutableList<Int> = mutableListOf()
        when (round) {
            1 -> images.addAll(
                listOf(
                    R.drawable.one,
                    R.drawable.two,
                    R.drawable.three,
                    R.drawable.four
                )
            )

            2 -> images.addAll(
                listOf(
                    R.drawable.one,
                    R.drawable.two,
                    R.drawable.three,
                    R.drawable.four,
                    R.drawable.five,
                    R.drawable.six
                )
            )

            3 -> images.addAll(
                listOf(
                    R.drawable.one,
                    R.drawable.two,
                    R.drawable.three,
                    R.drawable.four,
                    R.drawable.five,
                    R.drawable.six,
                    R.drawable.seven,
                    R.drawable.eight
                )
            )
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
        try {

            //WITHOUT THE POSSIBILITY TO FLIP THE CARDS BACK DOWN, TO BE DISCUSSED.
            val card = cards[position]
            if (card.isMatched || card.isFaceUp || card.isBlocked) return
            if (card.isBlocked) {
                ToastContextCallback("The card is temporarily blocked")
                return
            }
            if (indexOfSingleSelectedCard == null) {
                restoreCards()
                indexOfSingleSelectedCard = position
            } else {
                checkForMatch(indexOfSingleSelectedCard!!, position)
                indexOfSingleSelectedCard = null
            }

            if (round == 3) {
                // Se la carta è scoperta e viene cliccata, significa che verrà coperta
                if (card.isFaceUp) {
                    card.AvailableReveals--
                    if (card.AvailableReveals == 0) {
                        blockCard(position)
                    }
                }
            }

            card.isFaceUp = !card.isFaceUp
            updateViews()
            checkAllMatched()


            //WITH THE POSSIBILITY TO FLIP THE CARDS BACK DOWN, TO NOT BE DISCUSSED.

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
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error during card click handling", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }


    override fun saveState(savedState: Bundle) {
        try {
            savedState.putIntArray("cardIdentifiers", cards.map { it.identifier }.toIntArray())
            savedState.putBooleanArray("isFaceUp", cards.map { it.isFaceUp }.toBooleanArray())
            savedState.putBooleanArray("isMatched", cards.map { it.isMatched }.toBooleanArray())
            savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
            savedState.putBooleanArray("isBlocked", cards.map { it.isBlocked }.toBooleanArray())
            savedState.putIntArray(
                "AvailableReveals",
                cards.map { it.AvailableReveals }.toIntArray()
            )
            savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error saving state", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    override fun restoreState(savedInstanceState: Bundle) {
        try {
            val identifiers = savedInstanceState.getIntArray("cardIdentifiers")
            val isFaceUp = savedInstanceState.getBooleanArray("isFaceUp")
            val isMatched = savedInstanceState.getBooleanArray("isMatched")
            val isBlocked = savedInstanceState.getBooleanArray("isBlocked")
            val AvailableReveals = savedInstanceState.getIntArray("AvailableReveals")
            if (identifiers != null && isFaceUp != null && isMatched != null && isBlocked != null && AvailableReveals != null) {
                val savedCards = mutableListOf<MemoryCard>()
                for (index in identifiers.indices) {
                    val card = MemoryCard(
                        identifier = identifiers[index],
                        isFaceUp = isFaceUp[index],
                        isMatched = isMatched[index],
                        isBlocked = isBlocked[index],
                        AvailableReveals = AvailableReveals[index]
                    )
                    savedCards.add(card)
                }
                cards = savedCards
            }

            indexOfSingleSelectedCard =
                savedInstanceState.getInt("indexOfSingleSelectedCard").takeIf { it != -1 }
            updateViews()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error restoring state", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun restoreCards() {
        try {
            cards.forEach { card ->
                if (!card.isMatched && card.isFaceUp) {
                    card.isFaceUp = false
                    if (round == 3) {
                        card.AvailableReveals--
                        if (card.AvailableReveals == 0) {
                            blockCard(cards.indexOf(card))
                        }
                    }
                }
            }
            updateViews()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error restoring cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        try {
            if (cards[position1].identifier == cards[position2].identifier) {
                cards[position1].isMatched = true
                cards[position2].isMatched = true
                ToastContextCallback("Match found!")
            } else {
                handler.postDelayed({
                    cards[position1].isFaceUp = false
                    cards[position2].isFaceUp = false
                    if (round == 3) {
                        cards[position1].AvailableReveals--
                        cards[position2].AvailableReveals--
                        if (cards[position1].AvailableReveals == 0) {
                            blockCard(position1)
                        }
                        if (cards[position2].AvailableReveals == 0) {
                            blockCard(position2)
                        }
                    }
                    updateViews()
                }, 350)
            }
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error checking for match", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun updateViews() {
        try {


            updateViewsCallback(cards)
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error updating View", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun checkAllMatched() {
        try {
            if (cards.all { it.isMatched }) {
                onAllCardsMatchedCallback()
            }
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error checking all match", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    override fun getCards(): List<MemoryCard> {
        return try {
            cards
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error getting cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    override fun determineWinner(): Int? {
        return try {
            return null // Single player game does not determine a winner
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error determing winner", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }

    override fun getScore(player: String): Int {
        return try {
            return 0 // Single player game does not determine a winner
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error getting score", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }

    private fun blockCard(position: Int) {
        try {
            if (cards[position].isBlocked) return
            cards[position].isBlocked = true
            updateViews()
            object : CountDownTimer(4000, 10) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    cards[position].isBlocked = false
                    cards[position].AvailableReveals = 2
                    updateViews()
                }
            }.start()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error blocking card", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}


