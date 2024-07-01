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

    private val updateViewsCallback: (List<MemoryCard>) -> Unit, // Callback to update the view with the current cards
    private val onAllCardsMatchedCallback: () -> Unit, // Callback when all cards are matched
    private val toastContextCallback: (String) -> Unit, // Callback to show Toast messages
    private var round: Int, // Current round of the game
    private val numberOfCards: Int, // Number of cards in the game

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

    // Setup the game with cards based on the current round
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
        // Ensure the position is within the valid range before proceeding
        if (position < 0 || position >= cards.size) {
            Log.e("SingleGameLogic", "Invalid card position: $position")
            return
        }


        try {
            val card = cards[position]
            if (card.isMatched || card.isFaceUp || card.isBlocked) return
            indexOfSingleSelectedCard = if (indexOfSingleSelectedCard == null) {
                restoreCards()
                position
            } else {
                checkForMatch(indexOfSingleSelectedCard!!, position)  // Check if cards are the same
                null
            }

            if (round == 3) {
                // Handle card reveals for round 3,
                // if the card is face up and clicked, it will be flipped back down
                if (card.isFaceUp) {
                    card.availableReveals--
                    if (card.availableReveals == 0) {
                        blockCard(position)
                    }
                }
            }

            card.isFaceUp = !card.isFaceUp // Toggle the card face up/down state
            updateViews()
            checkAllMatched()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error during card click handling", e)
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
            savedState.putBooleanArray("isBlocked", cards.map { it.isBlocked }.toBooleanArray())
            savedState.putIntArray(
                "AvailableReveals",
                cards.map { it.availableReveals }.toIntArray()
            )
            savedState.putInt("indexOfSingleSelectedCard", indexOfSingleSelectedCard ?: -1)
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error saving state", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Restore the game state from a saved state
    override fun restoreState(savedInstanceState: Bundle) {
        try {
            val identifiers = savedInstanceState.getIntArray("cardIdentifiers")
            val isFaceUp = savedInstanceState.getBooleanArray("isFaceUp")
            val isMatched = savedInstanceState.getBooleanArray("isMatched")
            val isBlocked = savedInstanceState.getBooleanArray("isBlocked")
            val availableReveals = savedInstanceState.getIntArray("AvailableReveals")
            if (identifiers != null && isFaceUp != null && isMatched != null && isBlocked != null && availableReveals != null) {
                val savedCards = mutableListOf<MemoryCard>()
                for (index in identifiers.indices) {
                    val card = MemoryCard(
                        identifier = identifiers[index],
                        isFaceUp = isFaceUp[index],
                        isMatched = isMatched[index],
                        isBlocked = isBlocked[index],
                        availableReveals = availableReveals[index]
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

    // Restore the cards' state
    private fun restoreCards() {
        try {
            cards.forEach { card ->
                if (!card.isMatched && card.isFaceUp) {
                    card.isFaceUp = false
                    if (round == 3) {
                        card.availableReveals--
                        if (card.availableReveals == 0) {
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

    // Check if two cards match
    private fun checkForMatch(position1: Int, position2: Int) {
        if (position1 < 0 || position1 >= cards.size || position2 < 0 || position2 >= cards.size) {
            Log.e("SingleGameLogic", "Invalid card positions: $position1, $position2")
            return
        }
        try {
            if (cards[position1].identifier == cards[position2].identifier) {
                cards[position1].isMatched = true
                cards[position2].isMatched = true
                toastContextCallback("Good job!")
            } else {
                handler.postDelayed({
                    cards[position1].isFaceUp = false
                    cards[position2].isFaceUp = false
                    if (round == 3) {
                        cards[position1].availableReveals--
                        cards[position2].availableReveals--
                        if (cards[position1].availableReveals == 0) {
                            blockCard(position1)
                        }
                        if (cards[position2].availableReveals == 0) {
                            blockCard(position2)
                        }
                    }
                    updateViews()
                }, 300)
            }
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error checking for match", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Update the views with the current state of the cards
    private fun updateViews() {
        try {
            updateViewsCallback(cards)
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error updating View", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Check if all cards are matched
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

    // Get the current list of cards
    override fun getCards(): List<MemoryCard> {
        return try {
            cards
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error getting cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    // Determine the winner (not applicable for single-player game)
    override fun determineWinner(): Int? {
        return try {
            return null // Single player game does not determine a winner
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error determing winner", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }

    // Get the score for a player (not applicable for single-player game)
    override fun getScore(player: String): Int {
        return try {
            0 // Single player game does not determine a winner
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error getting score", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }

    // Block a card to prevent it from being flipped for 3 seconds (only for round 3)
    private fun blockCard(position: Int) {
        if (position < 0 || position >= cards.size) {
            Log.e("SingleGameLogic", "Invalid card position for blocking: $position")
            return
        }

        try {
            if (cards[position].isBlocked) return
            cards[position].isBlocked = true
            updateViews()
            object : CountDownTimer(4000, 10) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    cards[position].isBlocked = false
                    cards[position].availableReveals = 2
                    updateViews()
                }
            }.start()
        } catch (e: Exception) {
            Log.e("SingleGameLogic", "Error blocking card", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}


