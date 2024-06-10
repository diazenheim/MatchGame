package com.example.matchgame.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.telemetry.DataCollector

class Round1Fragment : Fragment() {

    private lateinit var dataCollector: DataCollector
    private val TAG = "Round1Fragment"
    private lateinit var buttons: List<ImageButton>
    private lateinit var gameLogic: GameLogic
    private var isGameLogicInitialized = false
    private lateinit var timer: CountDownTimer
    private var timeRemaining: Long = 30000 // Default time is 30 seconds
    private var startTime: Long = 0 // Variable to store start time
    private var isGameCompleted: Boolean = false // Track if the game is completed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.round1_layout, container, false)
        dataCollector = DataCollector(requireContext())
        startTime = System.currentTimeMillis() // Record the start time
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DataCollector
        //dataCollector = DataCollector(requireContext())

        // List of the image buttons from round1_layout.xml:
        buttons = listOf(
            view.findViewById(R.id.imgbtn1),
            view.findViewById(R.id.imgbtn2),
            view.findViewById(R.id.imgbtn3),
            view.findViewById(R.id.imgbtn4),
            view.findViewById(R.id.imgbtn5),
            view.findViewById(R.id.imgbtn6),
            view.findViewById(R.id.imgbtn7),
            view.findViewById(R.id.imgbtn8)
        )

        var timerTextView : TextView = view.findViewById(R.id.timerTextView) // Initialize the TextView for the timer

        // Initialize the game logic
        gameLogic = GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 1)
        isGameLogicInitialized = true

        // Restore the savedInstanceState
        savedInstanceState?.let {
            gameLogic.restoreState(it) // Restore the game state
            timeRemaining = it.getLong("timeRemaining", 30000) // Restore the timer state
        }

        for (index in buttons.indices) {
            val button = buttons[index]
            button.setOnClickListener {
                gameLogic.onCardClicked(index)
                // Log card flip event
                dataCollector.logCardFlipped(index)
                // Track button click event
                Log.i(TAG, "clicked the button")
            }
        }


        // Initialize and start the timer
        startTimer(timeRemaining)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel() // Cancelliamo il timer per evitare memory leaks
    }

    override fun onPause() {
        super.onPause()
        if (!isGameCompleted) {
            dataCollector.logGameAbandonment(level = 1) // Log game abandonment if the game is not completed
        }
    }

    private fun updateViews(cards: List<MemoryCard>) {
        for (index in cards.indices) {
            val card = cards[index]
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.1f
            } else {
                button.alpha = 1.0f
            }
            button.setImageResource(if (card.isFaceUp) card.identifier else R.drawable.card_down)
        }
    }
    private fun onAllCardsMatched() { //quando tutte carte sono matchate avviato il fragment relativo al round2
        val endTime = System.currentTimeMillis()
        val durationSeconds = (endTime - startTime) / 1000 // Convert milliseconds to seconds
        dataCollector.logLevelCompletionTime(1, durationSeconds) // Log level completion time
        isGameCompleted = true // Set the game as completed

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container, Round2Fragment())
            .commit()
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
                var timerTextView : TextView? = view?.findViewById(R.id.timerTextView)
                timerTextView?.text= "Tempo mancante: "+secondsRemaining.toString()+" secondi"
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