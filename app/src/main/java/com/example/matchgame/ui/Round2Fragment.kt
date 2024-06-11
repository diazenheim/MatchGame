package com.example.matchgame.ui

import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.adapter.CardAdapter
import com.example.matchgame.telemetry.DataCollector
import androidx.navigation.fragment.findNavController

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

    override fun onCardClicked(position: Int) {
        gameLogic.onCardClicked(position)
    }

    override fun onAllCardsMatched() {
        super.onAllCardsMatched()
        // Usa il NavController per navigare al round successivo
        findNavController().navigate(R.id.action_round2Fragment_to_round3Fragment)
    }
}









