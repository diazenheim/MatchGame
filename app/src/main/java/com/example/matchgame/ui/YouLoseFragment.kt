package com.example.matchgame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector
import com.google.firebase.crashlytics.FirebaseCrashlytics

// YouLoseFragment displays the screen when the player loses the game
class YouLoseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.youlose_layout, container, false)

            DataCollector.logGameEnd("lose")

            logTotalGameDuration()
            return view
        } catch (e: Exception) {
            Log.e("YouLoseFragment", "Error creating view", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    private fun logTotalGameDuration() {
        try {
            val endTime = System.currentTimeMillis()
            val totalGameDuration = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logTotalGameDuration(totalGameDuration) // Log total game duration
        } catch (e: Exception) {
            Log.e("YouLoseFragment", "Error logging total game duration", e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            val homeButton: ImageButton =
                view.findViewById(R.id.homebutton)//Questo button serve per tornare alla home
            homeButton.setOnClickListener {
                findNavController().navigate(R.id.action_youLoseFragment_to_homeFragment)
            }
        } catch (e: Exception) {
            Log.e("YouLoseFragment", "Error on view created", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}