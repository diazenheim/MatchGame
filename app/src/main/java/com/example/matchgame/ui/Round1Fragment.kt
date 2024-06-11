package com.example.matchgame.ui


import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.telemetry.DataCollector
import androidx.navigation.fragment.findNavController

class Round1Fragment : BaseRoundFragment() {
    override fun createGameLogic(): GameLogic {
        return GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 2, getNumberOfCards())
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

    override fun onCardClicked(position: Int) {
        gameLogic.onCardClicked(position)
    }

    override fun onAllCardsMatched() {
        super.onAllCardsMatched()

                // Visualizziamo il fragment: YouLoseFragment
                findNavController().navigate(R.id.action_round1Fragment_to_youLoseFragment)



    }


}