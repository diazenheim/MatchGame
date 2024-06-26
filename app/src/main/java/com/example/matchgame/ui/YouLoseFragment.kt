package com.example.matchgame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchgame.MainActivity
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector

//Quando l'utente vince il gioco, si visualizza questo fragment
class YouLoseFragment : Fragment() {

    private lateinit var dataCollector: DataCollector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.youlose_layout, container, false)

        DataCollector.logGameEnd("lose")

        logTotalGameDuration()
        return view
    }

    private fun logTotalGameDuration() {
        try {
            val endTime = System.currentTimeMillis()
            val totalGameDuration = (endTime - BaseRoundFragment.gameStartTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logTotalGameDuration(totalGameDuration) // Log total game duration
        } catch (e: Exception) {
            DataCollector.logError("Errore durante logTotalGameDuration di YouLoseFragment: ${e.message}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeButton: ImageButton = view.findViewById(R.id.homebutton)//Questo button serve per tornare alla home
        homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_youLoseFragment_to_homeFragment)
        }
    }
}