package com.example.matchgame.telemetry

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class DataCollector(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun trackButton(buttonName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            putString("custom_param", "button_click")
        }
        firebaseAnalytics.logEvent("button_click", bundle)
    }

    private fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun logGameStart() {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "game_start")
        }
        logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logGameEnd(result: String) {
        val bundle = Bundle().apply {
            putString("game_result", result)
        }
        logEvent("game_end", bundle)
    }

    fun logCardFlipped(cardId: Int) {
        val bundle = Bundle().apply {
            putInt("card_id", cardId)
        }
        logEvent("card_flipped", bundle)
    }

    //Track the time taken to complete each level or round.
    // Useful to understand the difficulty of each level.
    fun logLevelCompletionTime(level: Int, timeTaken: Long) {
        val bundle = Bundle().apply {
            putInt("level", level)
            putLong("time_taken", timeTaken)
        }
        logEvent("level_completion_time", bundle)
    }

    //Track the number of attempts a user takes to complete a level or round.
    // Helps in analyzing user engagement and the game's difficulty.
    fun logNumberOfAttempts(level: Int, attempts: Int) {
        val bundle = Bundle().apply {
            putInt("level", level)
            putInt("attempts", attempts)
        }
        logEvent("number_of_attempts", bundle)
    }

    //Log when a user exits a game before completion.
    //Provides insights into user drop-off points.
    fun logGameAbandonment(level: Int) {
        val bundle = Bundle().apply {
            putInt("level", level)
        }
        logEvent("game_abandonment", bundle)
    }

    //Track clicks on various buttons like menu buttons, retry, next level, etc.
    //Useful for UI/UX analysis.
    fun logButtonClick(buttonName: String) {
        val bundle = Bundle().apply {
            putString("button_name", buttonName)
        }
        logEvent("button_click", bundle)
    }

    //Log any errors or exceptions that occur.
    //Helps in debugging and improving the app's stability.
    fun logError(errorMessage: String) {
        val bundle = Bundle().apply {
            putString("error_message", errorMessage)
        }
        logEvent("error_occurred", bundle)
    }

    //Track user properties such as device type, OS version, and user demographics.
    //Useful for segmenting your user base.
    fun setUserProperty(propertyName: String, propertyValue: String) {
        firebaseAnalytics.setUserProperty(propertyName, propertyValue)
    }

    // Add more telemetry functions as needed, bitch
}
