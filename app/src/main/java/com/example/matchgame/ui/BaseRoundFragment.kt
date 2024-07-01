package com.example.matchgame.ui

import android.content.Context
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
import com.google.firebase.crashlytics.FirebaseCrashlytics

abstract class BaseRoundFragment : Fragment() {

    companion object {
        var gameStartTime: Long = 0
    }

    protected lateinit var gameLogic: IGameLogic
    protected lateinit var cardAdapter: CardAdapter
    protected lateinit var recyclerView: RecyclerView
    private var isGameLogicInitialized = false
    private lateinit var timer: CountDownTimer
    protected var timeRemaining: Long = 0 // Initialize with 0, will be set in onViewCreated
    private var startTime: Long = 0 // Variable to store start time
    private var isPaused: Boolean = false
    private val buttonClickCounts = mutableMapOf<Int, Int>() // Counter for button clicks
    private val buttonClickTimes = mutableListOf<Long>() // List to store button click times

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            // Inflate the layout for this fragment
            val view = inflater.inflate(getLayoutId(), container, false)

            // Record the start time of the game
            startTime = System.currentTimeMillis() // Record the start time of the game

            // Initialize game start time at the beginning of round 1 or multiplayer
            if (getLevel() == 1) {
                gameStartTime = startTime // Initialize game start time at the beginning of round 1
            } else if (this is MultiplayerFragment && gameStartTime == 0L) {
                gameStartTime = startTime // Initialize game start time at the beginning of multiplayer
            }

            // Set up the RecyclerView for displaying cards
            recyclerView = view.findViewById(R.id.recyclerView_round)
            setupRecyclerView()

            // Only pass getCurrentPlayer if this is a MultiplayerFragment
            cardAdapter = if (this is MultiplayerFragment) {
                CardAdapter(mutableListOf(), this::onCardClicked, this::getCurrentPlayer)
            } else {
                CardAdapter(mutableListOf(), this::onCardClicked)
            }
            recyclerView.adapter = cardAdapter

            view
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error onCreateView",e)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // Initialize game logic
            gameLogic = createGameLogic()
            isGameLogicInitialized = true
            timeRemaining = getTimerDuration() // Set the timeRemaining to the correct duration for the round
            savedInstanceState?.let {
                gameLogic.restoreState(it)
                timeRemaining = it.getLong("timeRemaining", timeRemaining) // Restore the timer state
            }

            // Start the timer if not in multiplayer mode
            if (this !is MultiplayerFragment) {
                startTimer(timeRemaining)
            } else {
                // Hide timer TextView if timer is not used
                val timerTextView: TextView? = view.findViewById(R.id.timerTextView)
                timerTextView?.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error onViewCreated",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (this !is MultiplayerFragment) {
                timer.cancel() // Cancel the timer to avoid memory leaks if not in multiplayer mode
            }
            sendButtonClickDataToFirebase()

        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error onDestroyView",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    protected fun updateViews(cards: List<MemoryCard>) { // Delegate updating the cards to the adapter
        try {
            cardAdapter.updateCards(cards)
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Errore updating views",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    // Setup RecyclerView with a GridLayoutManager based on orientation
    private fun setupRecyclerView() {
        try {
            val orientation = resources.configuration.orientation
            val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
            recyclerView.layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error onCreateView",e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Called when all cards are matched to log completion time and move to the next round
    protected open fun onAllCardsMatched() {
        try {
            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - startTime) / 1000 // Convert milliseconds to seconds
            if (this is MultiplayerFragment) {
                DataCollector.logMultiplayerCompletionTime(durationSeconds)
            } else {
                DataCollector.logLevelCompletionTime(getLevel(), durationSeconds)
            }
            Log.d("BaseRoundFragment", "onAllCardsMatched called, duration: ${durationSeconds}s")
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error matching all cards",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    // Display a Toast message from the game logic
    protected fun showToast(message: String) { //mostra i Toast provenienti dal GameLogic
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error showing toast",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    // Start a countdown timer for the game round
    private fun startTimer(timeInMillis: Long) {
        try {
            timer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(remainingTimeInMillis: Long) {
                    // Update the timer every 1000 milliseconds
                    timeRemaining = remainingTimeInMillis
                    val secondsRemaining = remainingTimeInMillis / 1000
                    val timerTextView: TextView? = view?.findViewById(R.id.timerTextView)
                    timerTextView?.text = getString(R.string.time_remaining, secondsRemaining)

                }

                override fun onFinish() {
                    // Handle timer finishing
                    showToast("Time's up!")
                    findNavController().navigate(R.id.youLoseFragment)
                }
            }.start() // Start the timer
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error starting time",e)
            FirebaseCrashlytics.getInstance().recordException(e) }
    }

    fun pauseTimer() {
        try{
            timer.cancel()
            isPaused = true
    } catch (e: Exception) {
            Log.e("BaseRoundFragment", "Error stopping timer", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun resumeTimer() {
        try {
            if (isPaused) {
                startTimer(timeRemaining)
                isPaused = false
            }
        } catch (e: Exception) {
            Log.e("BaseRoundFragment", "Error resuming timer", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Save the game state and remaining time
    override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)
        try {
            if (isGameLogicInitialized) { // Save GameLogic object only if initialized, otherwise we'd get an exception
                gameLogic.saveState(savedState)
            }
            // Save the remaining time
            savedState.putLong("timeRemaining", timeRemaining)
        } catch (e: Exception) {
            Log.e("BaseRoundFragment","Error saving the instance state",e)
            FirebaseCrashlytics.getInstance().recordException(e)}
    }

    // Abstract methods to be implemented by subclasses:
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
        if (this is MultiplayerFragment) {
            DataCollector.logMultiplayerButtonClickCounts(buttonClickCounts)
        } else {
            DataCollector.logButtonClickCounts(getLevel(), buttonClickCounts)
        }
        logAverageTimeBetweenClicks()
    }

    // Calculate and log average time between button clicks
    private fun logAverageTimeBetweenClicks() {
        if (buttonClickTimes.size > 1) {
            var totalTime: Long = 0
            for (i in 1 until buttonClickTimes.size) {
                totalTime += buttonClickTimes[i] - buttonClickTimes[i - 1]
            }
            val averageTime = totalTime / (buttonClickTimes.size - 1)
            val averageTimeInSeconds = averageTime / 1000.0
            if (this is MultiplayerFragment) {
                DataCollector.logMultiplayerAverageTimeBetweenClicks(averageTimeInSeconds)
            } else {
                DataCollector.logAverageTimeBetweenClicks(getLevel(), averageTimeInSeconds)
            }
            Log.d("BaseRoundFragment", "Average time between clicks: $averageTimeInSeconds seconds") // Log for debugging
        }
    }
}