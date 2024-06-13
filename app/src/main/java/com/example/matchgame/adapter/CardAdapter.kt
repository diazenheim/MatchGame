package com.example.matchgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard

// Manages the logic for displaying the cards in the RecyclerView used for the second round
class CardAdapter(
    private var cards: List<MemoryCard>,
    private val cardClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card, cardClickListener) //ottiene la carta alla posizione specificata e la passa al method bind
    }

    override fun getItemCount() = cards.size //ritorna il numero di carte

    fun updateCards(newCards: List<MemoryCard>) {
        cards = newCards
        notifyDataSetChanged() //notifico RecyclerView del cambio di carte
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //CardViewHolder contiene la logica per visualizzare e aggiornare una singola carta.
        private val imageButton: ImageButton = itemView.findViewById(R.id.imgbtn)

        fun bind(card: MemoryCard, cardClickListener: (Int) -> Unit) { //associa i dati della MemoryCard alla vista
            if (card.isFaceUp) imageButton.setImageResource(card.identifier)
            else imageButton.setImageResource(R.drawable.card_down)
            if(card.isMatched) imageButton.alpha = 0.1f
            else imageButton.alpha = 1.0f
            imageButton.setOnClickListener { cardClickListener(adapterPosition) }// Imposta il listener per il click, che chiamerà la callback con la posizione della carta
        }
    }
}