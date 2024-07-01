package com.example.matchgame.ui

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.matchgame.R
import com.example.matchgame.logic.IGameLogic
import com.example.matchgame.logic.SingleGameLogic
import com.google.firebase.crashlytics.FirebaseCrashlytics


class Round3Fragment : BaseRoundFragment() {
    private val availableRevealsForCard = mutableMapOf<Int, Int>()
    override fun createGameLogic(): IGameLogic {
        return try{
            SingleGameLogic(
                ::updateViews,
                ::onAllCardsMatched,
                this::showToast,
                3,
                getNumberOfCards())
    }catch (e: Exception) {
            Log.e("Round3Fragment", "Error creating game logic", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e}
    }

    override fun getLayoutId(): Int {
        return R.layout.round3_layout_container
    }

    override fun getLevel(): Int {
        return 3
    }

    override fun getNextRoundFragment(): Int {

        return R.id.youWinFragment
    }

    override fun getNumberOfCards(): Int {
        return 16 // Specifica il numero di carte per il round 2
    }

    override fun getTimerDuration(): Long {
        return 90000 // 90 seconds
    }

    override fun onCardClicked(position: Int) {
        try {
            val reveals = availableRevealsForCard[position] ?: 0
            if (reveals >= 2) { // Se una carta è già scoperta due volte, devo attendere
            } else {
                logButtonClick(position) // Log the button click
                gameLogic.onCardClicked(position)
            }
        } catch (e: Exception) {
            Log.e("Round3Fragment", "Error Clicking cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onAllCardsMatched() {
        try {
            super.onAllCardsMatched()

            // Visualizziamo il fragment: YouLoseFragment
            findNavController().navigate(R.id.action_round3Fragment_to_youWinFragment)

        } catch (e: Exception) {
            Log.e("Round3Fragment", "Error on all cards matched", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}

