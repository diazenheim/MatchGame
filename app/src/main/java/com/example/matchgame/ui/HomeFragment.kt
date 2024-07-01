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
import com.google.firebase.crashlytics.FirebaseCrashlytics

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
                    DataCollector.logClickPlayButtonTime(clickPlayButton, "single-player")
                    findNavController().navigate(R.id.action_homeFragment_to_round1Fragment)
                } catch(e:Exception){
                    Log.e("HomeFragment","Error setting on the solo play button",e)
                    FirebaseCrashlytics.getInstance().recordException(e)}
            }
            playMultiplayerButton.setOnClickListener {
                try {
                    //Send how much time is used to click the playButton
                    val clickPlayMultiButton = (System.currentTimeMillis() - startHomeFragmentTime) / 1000.0
                    DataCollector.logClickPlayButtonTime(clickPlayMultiButton, "multiplayer")
                    findNavController().navigate(R.id.action_homeFragment_to_multiplayerFragment)
                } catch(e:Exception){
                    Log.e("HomeFragment","Error setting on the multiplay button",e)
                    FirebaseCrashlytics.getInstance().recordException(e)}

            }
            menuButton.setOnClickListener {
                try {
                    showPopupMenu(it)
                }
                catch(e:Exception){
                    Log.e("HomeFragment","Error setting on the menu button",e)
                    FirebaseCrashlytics.getInstance().recordException(e)}
            }
        } catch(e:Exception){
            Log.e("HomeFragment","Error creating View",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
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
                    catch(e:Exception){
                        Log.e("HomeFragment","Error clicking about",e)
                        FirebaseCrashlytics.getInstance().recordException(e)}
                true}
                R.id.info -> {
                    // Handle choice three
                    try {
                        findNavController().navigate(R.id.action_homeFragment_to_infoFragment)
                    } catch(e:Exception){
                        Log.e("HomeFragment","Error clicking info",e)
                        FirebaseCrashlytics.getInstance().recordException(e)}
                    true
                } else -> false
            }
        }
        popupMenu.show()
    }
}
