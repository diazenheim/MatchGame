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

//Quando l'utente vince il gioco, si visualizza questo fragment
class YouWinFragment : Fragment() {

    private lateinit var dataCollector: DataCollector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.youwin_layout, container, false)

            // Mark the game as completed
            (activity as? MainActivity)?.onGameCompleted()

            DataCollector.logGameEnd("win")

            logTotalGameDuration()

            return view
        } catch (e: Exception) {
            Log.e("YouWinFragment", "Error creating view", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    private fun logTotalGameDuration() {
        try {
            val endTime = System.currentTimeMillis()
            val totalGameDuration = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logTotalGameDuration(totalGameDuration) // Log total game duration
        }catch (e: Exception) {
            Log.e("YouWinFragment", "Error logging game duration", e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {try{

        super.onViewCreated(view, savedInstanceState)
        val homeButton: ImageButton = view.findViewById(R.id.homebutton)//Questo button serve per tornare alla home
        homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_youWinFragment_to_homeFragment)
        }
    }catch (e: Exception) {
        Log.e("YouWinFragment", "Error on view created", e)
        FirebaseCrashlytics.getInstance().recordException(e)}
    }
}