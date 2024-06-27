package com.example.matchgame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchgame.MainActivity
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector

class Player1WinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.player1wins_layout, container, false)

        logMultiplayerCompletionTime()
        (activity as? MainActivity)?.onGameCompleted() // Mark the game as completed

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeButton: ImageButton = view.findViewById(R.id.homebutton)//Questo button serve per tornare alla home
        homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_player1WinFragment_to_homeFragment)
        }
    }
    private fun logMultiplayerCompletionTime() {
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logMultiplayerCompletionTime(durationSeconds)
            Log.d("Player1WinFragment", "Multiplayer game duration: ${durationSeconds}s")
        } catch (e: Exception) {
            DataCollector.logError("Errore durante logMultiplayerCompletionTime di Player1WinFragment: ${e.message}")
        }
    }
}
