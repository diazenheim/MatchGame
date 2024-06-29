package com.example.matchgame.ui


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.adapter.CardAdapter
import com.example.matchgame.logic.IGameLogic
import com.example.matchgame.logic.MultiplayerGameLogic
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MultiplayerFragment : BaseRoundFragment() {


    private lateinit var playerTurnTextView: TextView
    private lateinit var playerPointTextView: TextView
    private var currentPlayer = 1
    private var flipsThisTurn = 0
    private var point = 0

    override fun createGameLogic(): IGameLogic {
        return try {
            return MultiplayerGameLogic(
                ::updateViews,
                ::onAllCardsMatched,
                this::showToast,
                getNumberOfCards(),
                this::getCurrentPlayer,
                this::switchPlayer
            )
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error creating game logic", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.multiplayer_layout_container
    }

    override fun getLevel(): Int {
        return 0
    }

    override fun getNextRoundFragment(): Int {

        return R.id.youWinFragment
        //add winfragment and xml for multiplayer
    }

    override fun getNumberOfCards(): Int {
        return 22 // Specifica il numero di carte per il round 2
    }

    override fun getTimerDuration(): Long {
        return 0 // Return 0 as a default value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            playerTurnTextView = view.findViewById(R.id.playerTurnTextView)
            playerPointTextView = view.findViewById(R.id.counterPoint)

            updatePlayerTurn()
            cardAdapter =
                CardAdapter(gameLogic.getCards(), this::onCardClicked, this::getCurrentPlayer)
            recyclerView.adapter = cardAdapter
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error in onViewCreated", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    override fun onCardClicked(position: Int) {
        try {
            updatePlayerTurn()
            if (flipsThisTurn < 2) {
                flipsThisTurn++
                logButtonClick(position) // Log the button click
                gameLogic.onCardClicked(position)
                if (flipsThisTurn == 2) {
                    flipsThisTurn = 0
                }
            } else {
                showToast("You can flip only two cards per turn")
            }
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error clicking card", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onAllCardsMatched() {
        try {
            val winner = gameLogic.determineWinner()
            if (winner == 1) {
                findNavController().navigate(R.id.action_multiplayerFragment_to_player1WinFragment)
            } else {
                findNavController().navigate(R.id.action_multiplayerFragment_to_player2WinFragment)
            }
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error matching all cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun updatePlayerTurn() {
        try {
            playerTurnTextView.text = "Player $currentPlayer"
            playerPointTextView.text =
                "Score: ${gameLogic.getScore("$currentPlayer")}"
            val colorResId =
                if (currentPlayer == 1) R.color.red else R.color.blue
            playerPointTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    colorResId
                )
            )
            playerTurnTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    colorResId
                )
            )
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error updating turn player", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun switchPlayer() {
        try {
            currentPlayer = if (currentPlayer == 1) 2 else 1
            updatePlayerTurn()
        } catch (e: Exception) {
            Log.e(
                "MultiplayerFragment",
                "Error switching player",
                e
            )
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getCurrentPlayer(): Int {
        return try {
            return currentPlayer
        } catch (e: Exception) {
            Log.e("MultiplayerFragment", "Error getting current player", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            0
        }
    }
}