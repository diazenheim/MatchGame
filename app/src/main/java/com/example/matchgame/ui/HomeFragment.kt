package com.example.matchgame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.telemetry.DataCollector

class HomeFragment : Fragment() {

    private lateinit var dataCollector: DataCollector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.activity_main, container, false)
        dataCollector = DataCollector(requireContext())
        dataCollector.logGameStart()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the playModeButton in the layout
        val playModeButton: ImageButton = view.findViewById(R.id.play_button)

        // Set the listener for the play button
        playModeButton.setOnClickListener {
                // Se premo il button "play_mode" viene eseguito ilfragment: "UiFragment"
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main_container, Round1Fragment()) // fragment_main_container in questo momento funge da container per l'HomeFragment, con questa istruzione viene rimpiazzato con il fragment: "UiFragment"
                    .addToBackStack(null) //l'utente può premere il tasto "indietro" per tornare al HomeFragment dopo che UiFragment è stato visualizzato.
                    .commit() //Conferma la transazione del fragment
        }
    }
}