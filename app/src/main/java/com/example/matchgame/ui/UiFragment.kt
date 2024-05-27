package com.example.matchgame.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard

class UiFragment : Fragment() {

    private lateinit var buttons: List<ImageButton>
    private lateinit var gameLogic: GameLogic


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.play_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        gameLogic = GameLogic(buttons, ::updateViews)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {//Per ogni pulsante, viene assegnato un listener di clic
                Log.i(TAG, "clicked the button")
                gameLogic.onCardClicked(index)
            }
        }
        savedInstanceState?.let {
            gameLogic.restoreState(it) //se savedInstanceState non è nullo, si ripristina lo stato
        }
    }

    override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)
    }

    private fun updateViews(cards: List<MemoryCard>) {
        cards.forEachIndexed { index, card -> //itera su ogni carta nell'elenco cards, fornendo l'indice e la carta stessa a ogni iterazione
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.1f
            }
            button.setImageResource(if (card.isFaceUp) card.identifier else R.drawable.card_down) //imposta l'immagine del pulsante in base allo stato "isFaceUp" della carta
        }
    }
}
