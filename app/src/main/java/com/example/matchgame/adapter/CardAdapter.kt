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

    // Create a new ViewHolder for a card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        try {
            val card = cards[position]
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

    // Return the number of cards
    override fun getItemCount() = cards.size

    // Update the list of cards and notify the adapter
    fun updateCards(newCards: List<MemoryCard>) {
        try {
            cards = newCards
            notifyDataSetChanged() // Notify RecyclerView of the data change
        }catch(e:Exception){
            Log.e("CardAdapter", "Error updating cards", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // ViewHolder class for a single card
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageButton: ImageButton = itemView.findViewById(R.id.imgbtn)
        private val frameLayout: FrameLayout = itemView.findViewById(R.id.frame_layout)
        private val lockIcon: ImageView = itemView.findViewById(R.id.lock)

        // Bind the MemoryCard data to the view
        fun bind(card: MemoryCard, cardClickListener: (Int) -> Unit, getCurrentPlayer: (() -> Int)?) { // Associates the MemoryCard data with the view
            try{
                if (card.isFaceUp) {
                    imageButton.setImageResource(card.identifier)
                } else {
                    imageButton.setImageResource(R.drawable.card_down)
                }
                if (card.isBlocked) {
                    imageButton.colorFilter = desaturate()
                    lockIcon.visibility = View.VISIBLE // Show lock icon
                } else {
                    imageButton.colorFilter = null // Remove filter if card is not blocked
                    lockIcon.visibility = View.GONE // Hide lock icon
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

                // Set click listener for the card
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    imageButton.setOnClickListener { cardClickListener(position) }
                } else {
                    Log.e("CardViewHolder", "Invalid adapter position: $position")
                }
            } catch (e: Exception) {
                Log.e("CardViewHolder", "Error binding card", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        // Function to desaturate an image
        private fun desaturate(): ColorMatrixColorFilter { //funzione per desaturare immagine
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            return ColorMatrixColorFilter(matrix)
        }
    }
}