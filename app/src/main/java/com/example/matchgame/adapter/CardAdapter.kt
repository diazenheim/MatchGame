package com.example.matchgame.adapter

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.models.MemoryCard
import com.google.firebase.crashlytics.FirebaseCrashlytics

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
        try {
            val card = cards[position]
            //holder.bind(card, cardClickListener) //ottiene la carta alla posizione specificata e la passa al method bind
            holder.bind(
                card,
                cardClickListener,
                getCurrentPlayer
            ) // Pass getCurrentPlayer to the bind method if available

        } catch(e:Exception){

            Log.e("CardAdapter", "Error binding view holder at position $position", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun getItemCount() = cards.size //ritorna il numero di carte

    fun updateCards(newCards: List<MemoryCard>) {
        try {


            cards = newCards
            notifyDataSetChanged() //notifico RecyclerView del cambio di carte
        }catch(e:Exception){
            Log.e("CardAdapter", "Error updating cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //CardViewHolder contiene la logica per visualizzare e aggiornare una singola carta.

        private val imageButton: ImageButton = itemView.findViewById(R.id.imgbtn)
        private val frameLayout: FrameLayout = itemView.findViewById(R.id.frame_layout)
        private val lockIcon: ImageView = itemView.findViewById(R.id.lock)

        fun bind(card: MemoryCard, cardClickListener: (Int) -> Unit, getCurrentPlayer: (() -> Int)?) { // Associates the MemoryCard data with the view
            try{
            if (card.isFaceUp) {
                imageButton.setImageResource(card.identifier)
            } else {
                imageButton.setImageResource(R.drawable.card_down)
            }
            if (card.isBlocked) {
                imageButton.colorFilter = desaturate()
                lockIcon.visibility = View.VISIBLE // Mostra il lucchetto
            } else {
                imageButton.colorFilter = null // Rimuove il filtro se la carta non è bloccata
                lockIcon.visibility = View.GONE // Nasconde il lucchetto
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
        }catch(e:Exception){
                Log.e("CardViewHolder", "Error binding card", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
        private fun desaturate(): ColorMatrixColorFilter { //funzione per desaturare immagine
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            return ColorMatrixColorFilter(matrix)
        }
    }
}