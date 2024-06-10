package com.example.matchgame.ui

import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.adapter.CardAdapter


class Round2Fragment : Fragment() {

    private lateinit var gameLogic: GameLogic
    private lateinit var cardAdapter: CardAdapter
    private lateinit var recyclerView: RecyclerView
    private var isGameLogicInitialized = false
    private lateinit var timer: CountDownTimer
    private var timeRemaining: Long = 60000 // Default time is 90 seconds
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.round2_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_round2)
        setupRecyclerView()
        cardAdapter = CardAdapter(mutableListOf(), this::onCardClicked)
        recyclerView.adapter = cardAdapter

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameLogic = GameLogic(this::updateViews, this::onAllCardsMatched, this::showToast, 2)
        isGameLogicInitialized = true
        savedInstanceState?.let {
            gameLogic.restoreState(it)
            timeRemaining = it.getLong("timeRemaining", 60000) // Restore the timer state
        }
        Log.d("Round2Fragment", "onViewCreated called")
        startTimer(timeRemaining)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel() // Cancella il timer per evitare memory leaks
    }
    private fun updateViews(cards: List<MemoryCard>) { //delego l'aggiornamento delle carte alla classe Adapter
        Log.d("Round2Fragment", "Updating views with cards: $cards")
        cardAdapter.updateCards(cards)
    }
    private fun setupRecyclerView() { //a seconda che l'orientamento è portrait o landscape, lo scrolling nella RecycleView sarà orizzontale o verticale
        val orientation = resources.configuration.orientation
        recyclerView.layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        }

    }

    private fun onAllCardsMatched() { //quando tutte carte sono matchate avviato il fragment relativo al round2
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container, Round3Fragment())
            .commit()
    }

    private fun onCardClicked(position: Int) {
        gameLogic.onCardClicked(position)
    }

    private fun showToast(message: String) { //mostra i Toast provenienti dal GameLogic
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startTimer(timeInMillis: Long) {
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(RemainingTimeInMillis: Long) {
                // Aggiorna il timer ogni 1000 millisecondi(coundownInterval)
                timeRemaining = RemainingTimeInMillis
                val secondsRemaining = RemainingTimeInMillis / 1000
                var timerTextView: TextView? = view?.findViewById(R.id.timerTextView)
                timerTextView?.text = "Tempo mancante: " + secondsRemaining.toString() + " secondi"
                Log.i(ContentValues.TAG, "Seconds remaining: $secondsRemaining")
            }

            override fun onFinish() {
                // Questo metodo viene eseguito quando il timer termina
                showToast("Time's up!")
                // Visualizziamo il fragment: YouLoseFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main_container, YouLoseFragment())
                    .commit()
            }
        }.start() // Avviamo il timer
    }
    override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)
        if (isGameLogicInitialized) { //Salviamo l'oggetto GameLogic solo se è stato inizializzato, altrimenti avremmo un'eccezione
            gameLogic.saveState(savedState)
        }
        // Save the remaining time
        savedState.putLong("timeRemaining", timeRemaining)
    }

}