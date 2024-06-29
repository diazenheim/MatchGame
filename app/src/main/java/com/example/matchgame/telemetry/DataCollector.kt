package com.example.matchgame.telemetry

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics


object DataCollector {

    private lateinit var appContext: Context
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun initialize(context: Context){
        appContext = context.applicationContext
        firebaseAnalytics=FirebaseAnalytics.getInstance(appContext)
    }
    fun trackButton(buttonName: String) {
        ensureInitialized()
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            putString("custom_param", "button_click")
        }
        firebaseAnalytics.logEvent("button_click", bundle)
    }

    private fun logEvent(eventName: String, params: Bundle? = null) {
        ensureInitialized()
        firebaseAnalytics.logEvent(eventName, params)
        Log.d("DataCollector", "sent data to firebase console")
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
    fun logLevelCompletionTime(level: Int, timeTakenSeconds: Long) {
        val bundle = Bundle().apply {
            putInt("level", level)
            putLong("time_taken_seconds", timeTakenSeconds)
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
        ensureInitialized()
        firebaseAnalytics.setUserProperty(propertyName, propertyValue)
    }

    fun logRAMUsage() {
        val memoryInfo = getMemoryUsage()
        val availMemMB = memoryInfo.availMem / 1024.0 / 1024.0 // Convert to MB
        val totalMemGB = memoryInfo.totalMem / 1024.0 / 1024.0 / 1024.0 // Convert to GB
        val bundle = Bundle().apply {
            putDouble("avail_mem_MB", availMemMB)
            putDouble("total_mem_GB", totalMemGB)
            putBoolean("low_memory", memoryInfo.lowMemory)
        }
        logEvent("memory_usage", bundle)
    }

    private fun getMemoryUsage(): ActivityManager.MemoryInfo {
        val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    fun logTotalGameDuration(totalTimeSeconds: Long) {
        val bundle = Bundle().apply {
            putLong("total_game_duration", totalTimeSeconds)
        }
        logEvent("total_game_duration", bundle)
    }


    private fun ensureInitialized(){
        if(!::firebaseAnalytics.isInitialized){
            throw  UninitializedPropertyAccessException("Firebase must be initialized")
        }
    }

    fun getBatteryPercentage(context: Context): Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPercentage = (level / scale.toFloat() * 100).toInt()
        Log.d("DataCollector", "Battery percentage: $batteryPercentage%")
        return batteryPercentage
    }


    fun logBatteryUsage(initialBatteryPercentage: Int, endBatteryPercentage: Int) {
        val bundle = Bundle().apply {
            putInt("initial_battery_percentage", initialBatteryPercentage)
            putInt("end_battery_percentage", endBatteryPercentage)
            putInt("battery_usage_difference", initialBatteryPercentage - endBatteryPercentage)
        }
        Log.d("DataCollector", "Logging battery usage: Initial: $initialBatteryPercentage%, End: $endBatteryPercentage%, Difference: ${initialBatteryPercentage - endBatteryPercentage}%")
        logEvent("battery_usage", bundle)
    }

    //Log when a user exits a game before completion.
    //Provides insights into user drop-off points.
    fun logGameAbandonment(round: Int) {
        val bundle = Bundle().apply {
            putInt("abandoned_round", round)
        }
        logEvent("game_abandonment", bundle)
        Log.d("DataCollector", "Logging game abandonment at round $round")
    }


    fun logButtonClickCounts(level: Int, buttonClickCounts: Map<Int, Int>) {
        val params = Bundle().apply {
            buttonClickCounts.forEach { (buttonIndex, clickCount) ->
                putInt("button_${buttonIndex}_clicks", clickCount)
            }
        }
        logEvent("round${level}_button_clicks", params)
    }


    //Logs the launch time to Firebase
    fun logAppLaunchTime(launchTime: Double) {
        val bundle = Bundle().apply {
            putDouble("app_launch_time_seconds", launchTime)
        }
        logEvent("app_launch_time", bundle)
    }

    fun logClickPlayButtonTime(launchTime: Double, string: String) {
        val bundle = Bundle().apply {
            putDouble("click_play_button_seconds", launchTime)
            putString("button_type", string)
        }
        logEvent("click_play_button", bundle)
    }


    // Method to log the average time between card flips
    fun logAverageTimeBetweenClicks(level: Int, averageTime: Double) {
        val bundle = Bundle().apply {
            putDouble("average_time_between_clicks_seconds", averageTime)
        }
        logEvent("round${level}_average_time_between_clicks", bundle)
    }


    fun logMultiplayerCompletionTime(timeTakenSeconds: Long) {
        val bundle = Bundle().apply {
            putString("level", "multiplayer")
            putLong("time_taken_seconds", timeTakenSeconds)
        }
        logEvent("multiplayer_level_completion_time", bundle)
        Log.d("DataCollector", "Logged multiplayer_level_completion_time: level=multiplayer, time_taken_seconds=$timeTakenSeconds")
    }

    fun logMultiplayerButtonClickCounts(buttonClickCounts: Map<Int, Int>) {
        val params = Bundle().apply {
            buttonClickCounts.forEach { (buttonIndex, clickCount) ->
                putInt("button_${buttonIndex}_clicks", clickCount)
            }
        }
        logEvent("multiplayer_button_clicks", params)
    }


    fun logMultiplayerAverageTimeBetweenClicks(averageTime: Double) {
        val bundle = Bundle().apply {
            putDouble("average_time_between_clicks_seconds", averageTime)
        }
        logEvent("multiplayer_average_time_between_clicks", bundle)
    }



}
