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
import com.google.firebase.crashlytics.FirebaseCrashlytics

// Player2WinFragment displays the screen when player 2 wins the game
class Player2WinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            val view = inflater.inflate(R.layout.player2wins_layout, container, false)

            logMultiplayerCompletionTime()
            (activity as? MainActivity)?.onGameCompleted() // Mark the game as completed

            return view
        } catch (e: Exception) {
            Log.e("Player2WinFragment", "Error creating view", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            val homeButton: ImageButton =
                view.findViewById(R.id.homebutton)//Questo button serve per tornare alla home
            homeButton.setOnClickListener {
                findNavController().navigate(R.id.action_player2WinFragment_to_homeFragment)
            }
        } catch (e: Exception) {
            Log.e("Player2WinFragment", "Error on view created", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    private fun logMultiplayerCompletionTime() {
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logMultiplayerCompletionTime(durationSeconds)
            Log.d("Player2WinFragment", "Multiplayer game duration: ${durationSeconds}s")
        } catch (e: Exception) {
            Log.e("Player2WinFragment", "Error logging multiplayer time", e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }
}
