package com.example.matchgame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

// Manages the logic for displaying the cards in the RecyclerView used for the second round
class CardAdapter(
    private var cards: List<MemoryCard>,
    private val cardClickListener: (Int) -> Unit,
    private val getCurrentPlayer: (() -> Int)? = null // Function to get the current player
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        //holder.bind(card, cardClickListener) //ottiene la carta alla posizione specificata e la passa al method bind
        holder.bind(card, cardClickListener, getCurrentPlayer) // Pass getCurrentPlayer to the bind method if available
    }

    override fun getItemCount() = cards.size //ritorna il numero di carte

    fun updateCards(newCards: List<MemoryCard>) {
        cards = newCards
        notifyDataSetChanged() //notifico RecyclerView del cambio di carte
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //CardViewHolder contiene la logica per visualizzare e aggiornare una singola carta.

        private val imageButton: ImageButton = itemView.findViewById(R.id.imgbtn)
        private val frameLayout: FrameLayout = itemView.findViewById(R.id.frame_layout)


        fun bind(card: MemoryCard, cardClickListener: (Int) -> Unit, getCurrentPlayer: (() -> Int)?) { // Associates the MemoryCard data with the view
            if (card.isFaceUp) {
                imageButton.setImageResource(card.identifier)
            } else {
                imageButton.setImageResource(R.drawable.card_down)
            }

            if (card.isMatched) {
                frameLayout.alpha = 0.1f // Set higher transparency for matched cards
            } else {
                frameLayout.alpha = 1.0f
            }

            // Set the frame color based on the current player if getCurrentPlayer is available
            getCurrentPlayer?.let {
                frameLayout.setBackgroundColor(
                    if (it() == 1) Color.RED else Color.BLUE
                )
            }

            imageButton.setOnClickListener { cardClickListener(adapterPosition) } // Sets the click listener, which will call the callback with the card's position
        }
    }
}