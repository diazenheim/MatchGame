package com.example.matchgame.ui

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
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.adapter.CardAdapter
import com.example.matchgame.telemetry.DataCollector
import androidx.navigation.fragment.findNavController
import com.example.matchgame.logic.IGameLogic

abstract class BaseRoundFragment : Fragment() {

    companion object {
        var gameStartTime: Long = 0
    }


    protected lateinit var gameLogic: IGameLogic
    protected lateinit var cardAdapter: CardAdapter
    protected lateinit var recyclerView: RecyclerView
    protected var isGameLogicInitialized = false
    protected lateinit var timer: CountDownTimer
    protected var timeRemaining: Long = 0 // Initialize with 0, will be set in onViewCreated
    protected var startTime: Long = 0 // Variable to store start time
    protected var isGameCompleted: Boolean = false // Track if the game is completed

    private val buttonClickCounts = mutableMapOf<Int, Int>() // Counter for button clicks
    private val buttonClickTimes = mutableListOf<Long>() // List to store button click times


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            // Inflate the layout for this fragment
            val view = inflater.inflate(getLayoutId(), container, false)

            startTime = System.currentTimeMillis() // Record the start time of the game

            if (getLevel() == 1) {
                gameStartTime = startTime // Initialize game start time at the beginning of round 1
            }

            recyclerView = view.findViewById(R.id.recyclerView_round)
            setupRecyclerView()

            // Only pass getCurrentPlayer if this is a MultiplayerFragment
            cardAdapter = if (this is MultiplayerFragment) {
                CardAdapter(mutableListOf(), this::onCardClicked, (this as MultiplayerFragment)::getCurrentPlayer)
            } else {
                CardAdapter(mutableListOf(), this::onCardClicked)
            }
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
            timeRemaining = getTimerDuration() // Set the timeRemaining to the correct duration for the round
            savedInstanceState?.let {
                gameLogic.restoreState(it)
                timeRemaining = it.getLong("timeRemaining", timeRemaining) // Restore the timer state
            }

            if (this !is MultiplayerFragment) {
                startTimer(timeRemaining)
            } else {
                // Hide timer TextView if timer is not used
                val timerTextView: TextView? = view.findViewById(R.id.timerTextView)
                timerTextView?.visibility = View.GONE
            }

        } catch (e: Exception) {
            DataCollector.logError("Errore durante onViewCreated di BaseRoundFragment: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (this !is MultiplayerFragment) {
                timer.cancel() // Cancella il timer per evitare memory leaks
            }
            sendButtonClickDataToFirebase()

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

    protected fun setupRecyclerView() {
        try {
            val orientation = resources.configuration.orientation
            val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4 // Adjust based on your design
            recyclerView.layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante setupRecyclerView di BaseRoundFragment: ${e.message}")
        }
    }


    protected open fun onAllCardsMatched() { //quando tutte carte sono matchate avviato il fragment relativo al round successivo
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - startTime) / 1000 // Convert milliseconds to seconds
            DataCollector.logLevelCompletionTime(getLevel(), durationSeconds) // Log level completion time
            Log.d("BaseRoundFragment", "onAllCardsMatched called")
            isGameCompleted = true // Set the game as completed


            /*if (getNextRoundFragment() == R.id.youWinFragment) {
                val totalGameDuration = (endTime - gameStartTime) / 1000 // Convert milliseconds to seconds
                DataCollector.logTotalGameDuration(totalGameDuration)
            }*/

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

    /*override fun onPause() {
        super.onPause()
        if (!isGameCompleted) {
            try {
                DataCollector.logGameAbandonment(getLevel()) // Log game abandonment if the game is not completed
            } catch (e: Exception) {
                DataCollector.logError("Errore durante onPause di BaseRoundFragment: ${e.message}")
            }
        }
    }*/

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

    abstract fun createGameLogic(): IGameLogic
    abstract fun getLayoutId(): Int
    abstract fun getLevel(): Int
    abstract fun getNextRoundFragment(): Int
    abstract fun getNumberOfCards(): Int
    abstract fun onCardClicked(position: Int)
    abstract fun getTimerDuration(): Long // Abstract method for timer duration

    protected fun logButtonClick(buttonIndex: Int) {
        val buttonIndexOneBased = buttonIndex + 1
        buttonClickCounts[buttonIndexOneBased] = buttonClickCounts.getOrDefault(buttonIndexOneBased, 0) + 1
        buttonClickTimes.add(System.currentTimeMillis()) // Add the current time to the list
        Log.d("BaseRoundFragment", "Button $buttonIndexOneBased clicked, current count: ${buttonClickCounts[buttonIndexOneBased]}") // Log for debugging
    }

    private fun sendButtonClickDataToFirebase() {
        Log.d("BaseRoundFragment", "Button click counts: $buttonClickCounts") // Log for debugging
        DataCollector.logButtonClickCounts(getLevel(), buttonClickCounts)
        logAverageTimeBetweenClicks()
    }

    private fun logAverageTimeBetweenClicks() {
        if (buttonClickTimes.size > 1) {
            var totalTime: Long = 0
            for (i in 1 until buttonClickTimes.size) {
                totalTime += buttonClickTimes[i] - buttonClickTimes[i - 1]
            }
            val averageTime = totalTime / (buttonClickTimes.size - 1)
            val averageTimeInSeconds = averageTime / 1000.0
            DataCollector.logAverageTimeBetweenClicks(getLevel(), averageTimeInSeconds)
            Log.d("BaseRoundFragment", "Average time between clicks: $averageTimeInSeconds seconds") // Log for debugging
        }
    }

}