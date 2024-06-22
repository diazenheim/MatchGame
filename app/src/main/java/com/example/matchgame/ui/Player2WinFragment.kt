package com.example.matchgame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.matchgame.MainActivity
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector

class Player2WinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.player2wins_layout, container, false)

        logMultiplayerCompletionTime()
        (activity as? MainActivity)?.onGameCompleted() // Mark the game as completed

        return view
    }

    private fun logMultiplayerCompletionTime() {
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logMultiplayerCompletionTime(durationSeconds)
            Log.d("Player2WinFragment", "Multiplayer game duration: ${durationSeconds}s")
        } catch (e: Exception) {
            DataCollector.logError("Errore durante logMultiplayerCompletionTime di Player2WinFragment: ${e.message}")
        }
    }
}
