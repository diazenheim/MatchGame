package com.example.matchgame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector
import androidx.navigation.fragment.findNavController
import android.util.Log

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("mainActivity", "AppStarted")

            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.home_layout, container, false)
            Log.d("mainActivity","gay")
            //DataCollector.logGameStart()
        Log.d("mainActivity", "2")
            return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            Log.d("mainActivity", "3")


            // Find the playModeButton in the layout
            val playModeButton: ImageButton = view.findViewById(R.id.play_button)
            Log.d("mainActivity", "3.5")
            // Set the listener for the play button
            playModeButton.setOnClickListener {
                try {
                    Log.d("mainActivity", "4")
                    findNavController().navigate(R.id.action_homeFragment_to_round1Fragment)
                }catch(e: Exception){
                    DataCollector.logError("Errore durante il click del playButton: ${e.message}")
                }
            }
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onViewCreated: ${e.message}")
        }
    }
}
