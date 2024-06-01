package com.example.matchgame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchgame.R
import com.example.matchgame.logic.GameLogic
import com.example.matchgame.models.MemoryCard
import com.example.matchgame.adapter.CardAdapter

class Round2Fragment : Fragment() {

    private lateinit var gameLogic: GameLogic
    private lateinit var cardAdapter: CardAdapter //adattatore delle carte per la RecycleView
    private lateinit var recyclerView: RecyclerView //permette lo scrolling del layout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.round2_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_round2) //trova la recyclerView dichiarata nele layout del round2
        recyclerView.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.HORIZONTAL, false) //imposta un GridLayoutManager con 4 colonne e scrolling orizzontale

        cardAdapter = CardAdapter(mutableListOf(), this::onCardClicked) //crea un CardAdapter con la lista di carte vuota e la callback onCardClicked
        recyclerView.adapter = cardAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameLogic = GameLogic(this::updateViews, this::onAllCardsMatched, this::showToast,2)
        Log.d("Round2Fragment", "onViewCreated called")
    }

    private fun onAllCardsMatched() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container, HomeFragment()) //quando tutte le carte sono matchate si torna alla home
            .addToBackStack(null)
            .commit()
    }

    private fun updateViews(cards: List<MemoryCard>) { //invochiamo il metodo di aggiornamento delle carte nell'adapter
        Log.d("Round2Fragment", "Updating views with cards: $cards")
        cardAdapter.updateCards(cards)
    }

    private fun onCardClicked(position: Int) { //deleghiamo la gestione del click delle carte a GameLogic
        gameLogic.onCardClicked(position)
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}