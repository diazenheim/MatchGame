package com.example.matchgame.ui


import android.util.Log
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.IGameLogic
import com.example.matchgame.logic.SingleGameLogic
import com.google.firebase.crashlytics.FirebaseCrashlytics

class Round1Fragment : BaseRoundFragment() {

    // Create the game logic for round 1
    override fun createGameLogic(): IGameLogic {
        return try {
            SingleGameLogic(
                ::updateViews,
                ::onAllCardsMatched,
                this::showToast,
                1,
                getNumberOfCards()
            )
        } catch (e: Exception) {
            Log.e("Round1Fragment", "Error creating game logic", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    // Return the layout resource ID for round 1
    override fun getLayoutId(): Int {
        return R.layout.round1_layout_container
    }

    // Return the current level
    override fun getLevel(): Int {
        return 1
    }

    // Return the ID of the next fragment to navigate to when all cards are matched
    override fun getNextRoundFragment(): Int {

        return R.id.round2Fragment
    }

    // Return the number of cards for round 1
    override fun getNumberOfCards(): Int {
        return 8
    }

    // Return the timer duration for round 1
    override fun getTimerDuration(): Long {
        return 30000 // 30 seconds
    }

    // Handle card click events
    override fun onCardClicked(position: Int) {
        try {
            logButtonClick(position) // Log the button click
            gameLogic.onCardClicked(position)
        } catch (e: Exception) {
            Log.e("Round1Fragment", "Error clicking cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Handle the event when all cards are matched
    override fun onAllCardsMatched() {
        try {
            super.onAllCardsMatched()
            findNavController().navigate(R.id.action_round1Fragment_to_round2Fragment)
        } catch (e: Exception) {
            Log.e("Round1Fragment", "Error matching all card", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}