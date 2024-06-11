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
import com.example.matchgame.telemetry.DataCollector
import androidx.navigation.fragment.findNavController

abstract class BaseRoundFragment : Fragment() {

    protected lateinit var gameLogic: GameLogic
    protected lateinit var cardAdapter: CardAdapter
    protected lateinit var recyclerView: RecyclerView
    protected var isGameLogicInitialized = false
    protected lateinit var timer: CountDownTimer
    protected var timeRemaining: Long = 30000 // Default time, override in specific round fragments
    protected var startTime: Long = 0 // Variable to store start time
    protected var isGameCompleted: Boolean = false // Track if the game is completed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            // Inflate the layout for this fragment
            val view = inflater.inflate(getLayoutId(), container, false)
            startTime = System.currentTimeMillis() // Record the start time

            recyclerView = view.findViewById(R.id.recyclerView_round)
            setupRecyclerView()
            cardAdapter = CardAdapter(mutableListOf(), this::onCardClicked)
            recyclerView.adapter = cardAdapter

            view
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onCreateView di BaseRoundFragment: ${e.message}")
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            gameLogic = createGameLogic()
            isGameLogicInitialized = true
            savedInstanceState?.let {
                gameLogic.restoreState(it)
                timeRemaining = it.getLong("timeRemaining", timeRemaining) // Restore the timer state
            }

            startTimer(timeRemaining)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onViewCreated di BaseRoundFragment: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            timer.cancel() // Cancella il timer per evitare memory leaks
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onDestroyView di BaseRoundFragment: ${e.message}")
        }
    }

    protected fun updateViews(cards: List<MemoryCard>) { //delego l'aggiornamento delle carte alla classe Adapter
        try {

            cardAdapter.updateCards(cards)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante updateViews di BaseRoundFragment: ${e.message}")
        }
    }

    protected fun setupRecyclerView() { //a seconda che l'orientamento è portrait o landscape, lo scrolling nella RecycleView sarà orizzontale o verticale
        try {
            val orientation = resources.configuration.orientation
            recyclerView.layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            } else {
                GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
            }
        } catch (e: Exception) {
            DataCollector.logError("Errore durante setupRecyclerView di BaseRoundFragment: ${e.message}")
        }
    }

    protected open fun onAllCardsMatched() { //quando tutte carte sono matchate avviato il fragment relativo al round successivo
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - startTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logLevelCompletionTime(getLevel(), durationSeconds) // Log level completion time
            //isGameCompleted = true // Set the game as completed


        } catch (e: Exception) {
            DataCollector.logError("Errore durante onAllCardsMatched di BaseRoundFragment: ${e.message}")
        }
    }

    protected fun showToast(message: String) { //mostra i Toast provenienti dal GameLogic
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            DataCollector.logError("Errore durante showToast di BaseRoundFragment: ${e.message}")
        }
    }

    private fun startTimer(timeInMillis: Long) {
        try {
            timer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(RemainingTimeInMillis: Long) {
                    // Aggiorna il timer ogni 1000 millisecondi(coundownInterval)
                    timeRemaining = RemainingTimeInMillis
                    val secondsRemaining = RemainingTimeInMillis / 1000
                    val timerTextView: TextView? = view?.findViewById(R.id.timerTextView)
                    timerTextView?.text = getString(R.string.time_remaining, secondsRemaining)

                }

                override fun onFinish() {
                    // Questo metodo viene eseguito quando il timer termina
                    showToast("Time's up!")
                    // Visualizziamo il fragment: YouLoseFragment
                    findNavController().navigate(R.id.youLoseFragment)
                }
            }.start() // Avviamo il timer
        } catch (e: Exception) {
            DataCollector.logError("Errore durante startTimer di BaseRoundFragment: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isGameCompleted) {
            try {
                DataCollector.logGameAbandonment(getLevel()) // Log game abandonment if the game is not completed
            } catch (e: Exception) {
                DataCollector.logError("Errore durante onPause di BaseRoundFragment: ${e.message}")
            }
        }
    }

    override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)
        try {
            if (isGameLogicInitialized) { //Salviamo l'oggetto GameLogic solo se è stato inizializzato, altrimenti avremmo un'eccezione
                gameLogic.saveState(savedState)
            }
            // Save the remaining time
            savedState.putLong("timeRemaining", timeRemaining)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante onSaveInstanceState di BaseRoundFragment: ${e.message}")
        }
    }

    abstract fun createGameLogic(): GameLogic
    abstract fun getLayoutId(): Int
    abstract fun getLevel(): Int
    abstract fun getNextRoundFragment(): Int
    abstract fun getNumberOfCards(): Int
    abstract fun onCardClicked(position: Int)

}