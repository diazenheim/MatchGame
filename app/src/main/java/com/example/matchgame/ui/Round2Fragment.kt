package com.example.matchgame.ui

import android.util.Log
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.IGameLogic
import com.example.matchgame.logic.SingleGameLogic
import com.google.firebase.crashlytics.FirebaseCrashlytics

class Round2Fragment : BaseRoundFragment() {
    override fun createGameLogic(): IGameLogic {
        return try {
            SingleGameLogic(
                ::updateViews,
                ::onAllCardsMatched,
                this::showToast,
                2,
                getNumberOfCards()
            )
        } catch (e: Exception) {
            Log.e("Round2Fragment", "Error creating game logic", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.round2_layout_container
    }

    override fun getLevel(): Int {
        return 2
    }

    override fun getNextRoundFragment(): Int {

        return R.id.round3Fragment
    }

    override fun getNumberOfCards(): Int {
        return 12 // Specifica il numero di carte per il round 2
    }

    override fun getTimerDuration(): Long {
        return 60000 // 60 seconds
    }

    override fun onCardClicked(position: Int) {
        try {

            logButtonClick(position) // Log the button click
            gameLogic.onCardClicked(position)
        } catch (e: Exception) {
            Log.e("Round2Fragment", "Error clicking cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onAllCardsMatched() {
        try {
            super.onAllCardsMatched()
            // Usa il NavController per navigare al round successivo
            findNavController().navigate(R.id.action_round2Fragment_to_round3Fragment)
        } catch (e: Exception) {
            Log.e("Round2Fragment", "Error on all card matched", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}