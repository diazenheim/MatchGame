package com.example.matchgame.logic

import android.os.Bundle
import com.example.matchgame.models.MemoryCard

interface IGameLogic {
    fun onCardClicked(position: Int)
    fun saveState(savedState: Bundle)
    fun restoreState(savedInstanceState: Bundle)
    fun getCards(): List<MemoryCard>
    fun determineWinner(): Int?
}
