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
import android.view.ContextThemeWrapper
import android.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.d("mainActivity", "AppStarted")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_layout, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // Save the time when the Fragment is shown
            val startHomeFragmentTime: Long = System.currentTimeMillis()
            // Find the playModeButton in the layout
            val playModeButton: ImageButton = view.findViewById(R.id.play_button)
            val playMultiplayerButton: ImageButton =view.findViewById(R.id.play_multiplayer)
            val menuButton: ImageButton = view.findViewById(R.id.menu)
            // Set the listener for the play button
            playModeButton.setOnClickListener {
                try {
                    //Send how much time is used to click the playButton
                    val clickPlayButton = (System.currentTimeMillis() - startHomeFragmentTime) / 1000.0
                    DataCollector.logClickPlayButtonTime(clickPlayButton)
                    findNavController().navigate(R.id.action_homeFragment_to_round1Fragment)
                }catch(e: Exception){
                    DataCollector.logError("Errore durante il click del playButton: ${e.message}")
                }
            }
            playMultiplayerButton.setOnClickListener {
                try {
                    //Send how much time is used to click the playButton
                    //val clickMultiplayerButton = (System.currentTimeMillis() - startHomeFragmentTime) / 1000.0
                    //DataCollector.logClickPlayButtonTime(clickPlayButton)
                    findNavController().navigate(R.id.action_homeFragment_to_multiplayerFragment)
                }catch(e: Exception){
                    DataCollector.logError("Errore durante il click del playButton: ${e.message}")
                }
            }
            menuButton.setOnClickListener {
                try {
                    showPopupMenu(it)
                }
                catch(e: Exception){
                    DataCollector.logError("Errore durante il click del playButton: ${e.message}")
                }
            }
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onViewCreated: ${e.message}")
        }
    }
    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu), anchor)
        popupMenu.menuInflater.inflate(R.menu.pop_up_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about -> {
                    try{

                    findNavController().navigate(R.id.action_homeFragment_to_aboutFragment)

                }
                catch(e: Exception){
                    DataCollector.logError("Errore durante il click dell'about button: ${e.message}")
                }
                true}
                R.id.info -> {
                    // Handle choice three

                    try {

                        findNavController().navigate(R.id.action_homeFragment_to_infoFragment)

                    } catch (e: Exception) {
                        DataCollector.logError("Errore durante il click dell'info button: ${e.message}")
                    }
                    true
                }else -> false
            }
        }
        popupMenu.show()
    }
}
