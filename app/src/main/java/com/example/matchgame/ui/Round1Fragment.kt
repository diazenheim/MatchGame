package com.example.matchgame.ui


import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.GameLogic

class Round1Fragment : BaseRoundFragment() {
    override fun createGameLogic(): GameLogic {
        return GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 1, getNumberOfCards())
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
        logButtonClick(position) // Log the button click
        gameLogic.onCardClicked(position)
    }

    override fun onAllCardsMatched() {
        super.onAllCardsMatched()

        // Visualizziamo il fragment: YouLoseFragment
        findNavController().navigate(R.id.action_round1Fragment_to_round2Fragment)
    }

}