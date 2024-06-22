package com.example.matchgame.ui

import androidx.navigation.fragment.findNavController
import com.example.matchgame.R

import com.example.matchgame.adapter.CardAdapter
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.telemetry.DataCollector


class Round3Fragment : BaseRoundFragment() {
    override fun createGameLogic(): GameLogic {
        return GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 3, getNumberOfCards())
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
        logButtonClick(position) // Log the button click
        gameLogic.onCardClicked(position)
    }

    override fun onAllCardsMatched() {
        super.onAllCardsMatched()

        // Visualizziamo il fragment: YouLoseFragment
        findNavController().navigate(R.id.action_round3Fragment_to_youWinFragment)

    }
}

