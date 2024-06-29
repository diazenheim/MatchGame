package com.example.matchgame.ui


import android.util.Log
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.IGameLogic
import com.example.matchgame.logic.SingleGameLogic
import com.google.firebase.crashlytics.FirebaseCrashlytics

class Round1Fragment : BaseRoundFragment() {
    override fun createGameLogic(): IGameLogic {
        return try {
            return SingleGameLogic(
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

    override fun getLayoutId(): Int {
        return R.layout.round1_layout_container
    }

    override fun getLevel(): Int {
        return 1
    }

    override fun getNextRoundFragment(): Int {

        return R.id.round2Fragment
    }

    override fun getNumberOfCards(): Int {
        return 8 // Specifica il numero di carte per il round 2
    }

    override fun getTimerDuration(): Long {
        return 30000 // 30 seconds
    }

    override fun onCardClicked(position: Int) {
        try {
            logButtonClick(position) // Log the button click
            gameLogic.onCardClicked(position)
        } catch (e: Exception) {
            Log.e("Round1Fragment", "Error clicking cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onAllCardsMatched() {
        try {
            super.onAllCardsMatched()

            // Visualizziamo il fragment: YouLoseFragment
            findNavController().navigate(R.id.action_round1Fragment_to_round2Fragment)
        } catch (e: Exception) {
            Log.e("Round1Fragment", "Error matching all card", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}