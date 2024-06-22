package com.example.matchgame.ui


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.matchgame.R
import androidx.navigation.fragment.findNavController
import com.example.matchgame.adapter.CardAdapter
import com.example.matchgame.logic.GameLogic

class MultiplayerFragment : BaseRoundFragment() {

    private lateinit var playerTurnTextView: TextView
    private var currentPlayer = 1
    private var flipsThisTurn = 0

    override fun createGameLogic(): GameLogic {
        return GameLogic(::updateViews, ::onAllCardsMatched, this::showToast, 0, getNumberOfCards(), this::getCurrentPlayer)
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
        super.onViewCreated(view, savedInstanceState)
        playerTurnTextView = view.findViewById(R.id.playerTurnTextView)
        updatePlayerTurn()
        cardAdapter = CardAdapter(gameLogic.getCards(), this::onCardClicked, this::getCurrentPlayer)
        recyclerView.adapter = cardAdapter
    }


    override fun onCardClicked(position: Int) {
        if (flipsThisTurn < 2) {
            flipsThisTurn++
            logButtonClick(position) // Log the button click
            gameLogic.onCardClicked(position)
            if (flipsThisTurn == 2) {
                // Switch turn after 2 flips
                flipsThisTurn = 0
                currentPlayer = if (currentPlayer == 1) 2 else 1
                updatePlayerTurn()
            }
        } else {
            showToast("You can flip only two cards per turn")
        }
    }

    override fun onAllCardsMatched() {
        val winner = gameLogic.determineWinner()
        if (winner == 1) {
            findNavController().navigate(R.id.action_multiplayerFragment_to_player1WinFragment)
        } else {
            findNavController().navigate(R.id.action_multiplayerFragment_to_player2WinFragment)
        }
    }

    private fun updatePlayerTurn() {
        playerTurnTextView.text = "Player $currentPlayer's Turn"
        val colorResId = if (currentPlayer == 1) R.color.red else R.color.blue
        playerTurnTextView.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
    }

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }
}