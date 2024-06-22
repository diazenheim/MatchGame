package com.example.matchgame.ui

import android.util.Log
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.GameLogic

class Round2Fragment : BaseRoundFragment() {
    override fun createGameLogic(): GameLogic {
        return GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 2, getNumberOfCards())
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
        logButtonClick(position) // Log the button click
        gameLogic.onCardClicked(position)
    }

    override fun onAllCardsMatched() {
        super.onAllCardsMatched()
        // Usa il NavController per navigare al round successivo
        findNavController().navigate(R.id.action_round2Fragment_to_round3Fragment)
        Log.d("Round2Fragment", "Dove sono")
    }
}