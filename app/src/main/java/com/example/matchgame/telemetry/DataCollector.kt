package com.example.matchgame.telemetry

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

// Object for collecting and logging telemetry data to Firebase
object DataCollector {

    private lateinit var appContext: Context
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // Initialize the DataCollector with application context and FirebaseAnalytics instance
    fun initialize(context: Context){
        appContext = context.applicationContext
        firebaseAnalytics=FirebaseAnalytics.getInstance(appContext)
    }

    // Log an event to Firebase Analytics
    private fun logEvent(eventName: String, params: Bundle? = null) {
        ensureInitialized()
        firebaseAnalytics.logEvent(eventName, params)
        Log.d("DataCollector", "sent data to firebase console")
    }

    // Log the end of the game with the result
    fun logGameEnd(result: String) {
        val bundle = Bundle().apply {
            putString("game_result", result)
        }
        logEvent("game_end", bundle)
    }

    // Log the time taken to complete a level or round
    // Useful to understand the difficulty of each level
    fun logLevelCompletionTime(level: Int, timeTakenSeconds: Long) {
        val bundle = Bundle().apply {
            putInt("level", level)
            putLong("time_taken_seconds", timeTakenSeconds)
        }
        logEvent("level_completion_time", bundle)
    }


    // Set a user property for segmenting the user base
    fun setUserProperty(propertyName: String, propertyValue: String) {
        ensureInitialized()
        firebaseAnalytics.setUserProperty(propertyName, propertyValue)
    }

    // Log RAM usage data
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

    // Get memory usage information
    private fun getMemoryUsage(): ActivityManager.MemoryInfo {
        val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    // Log the total game duration
    fun logTotalGameDuration(totalTimeSeconds: Long) {
        val bundle = Bundle().apply {
            putLong("total_game_duration", totalTimeSeconds)
        }
        logEvent("total_game_duration", bundle)
    }

    // Ensure Firebase Analytics is initialized
    private fun ensureInitialized(){
        if(!::firebaseAnalytics.isInitialized){
            throw  UninitializedPropertyAccessException("Firebase must be initialized")
        }
    }

    // Get the current battery percentage
    fun getBatteryPercentage(context: Context): Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPercentage = (level / scale.toFloat() * 100).toInt()
        Log.d("DataCollector", "Battery percentage: $batteryPercentage%")
        return batteryPercentage
    }

    // Log battery usage data
    fun logBatteryUsage(initialBatteryPercentage: Int, endBatteryPercentage: Int) {
        val bundle = Bundle().apply {
            putInt("initial_battery_percentage", initialBatteryPercentage)
            putInt("end_battery_percentage", endBatteryPercentage)
            putInt("battery_usage_difference", initialBatteryPercentage - endBatteryPercentage)
        }
        Log.d("DataCollector", "Logging battery usage: Initial: $initialBatteryPercentage%, End: $endBatteryPercentage%, Difference: ${initialBatteryPercentage - endBatteryPercentage}%")
        logEvent("battery_usage", bundle)
    }

    // Log the number of button clicks in a round
    fun logButtonClickCounts(level: Int, buttonClickCounts: Map<Int, Int>) {
        val params = Bundle().apply {
            buttonClickCounts.forEach { (buttonIndex, clickCount) ->
                putInt("button_${buttonIndex}_clicks", clickCount)
            }
        }
        logEvent("round${level}_button_clicks", params)
    }


    // Log the app launch time
    fun logAppLaunchTime(launchTime: Double) {
        val bundle = Bundle().apply {
            putDouble("app_launch_time_seconds", launchTime)
        }
        logEvent("app_launch_time", bundle)
    }

    // Log the time when the play button is clicked (single player or multiplayer)
    fun logClickPlayButtonTime(launchTime: Double, string: String) {
        val bundle = Bundle().apply {
            putDouble("click_play_button_seconds", launchTime)
            putString("button_type", string)
        }
        logEvent("click_play_button", bundle)
    }


    // Log the average time between card flips
    fun logAverageTimeBetweenClicks(level: Int, averageTime: Double) {
        val bundle = Bundle().apply {
            putDouble("average_time_between_clicks_seconds", averageTime)
        }
        logEvent("round${level}_average_time_between_clicks", bundle)
    }

    // Log the time taken to complete a multiplayer game
    fun logMultiplayerCompletionTime(timeTakenSeconds: Long) {
        val bundle = Bundle().apply {
            putString("level", "multiplayer")
            putLong("time_taken_seconds", timeTakenSeconds)
        }
        logEvent("multiplayer_level_completion_time", bundle)
        Log.d("DataCollector", "Logged multiplayer_level_completion_time: level=multiplayer, time_taken_seconds=$timeTakenSeconds")
    }

    // Log the number of button clicks in a multiplayer game
    fun logMultiplayerButtonClickCounts(buttonClickCounts: Map<Int, Int>) {
        val params = Bundle().apply {
            buttonClickCounts.forEach { (buttonIndex, clickCount) ->
                putInt("button_${buttonIndex}_clicks", clickCount)
            }
        }
        logEvent("multiplayer_button_clicks", params)
    }

    // Log the average time between card flips in a multiplayer game
    fun logMultiplayerAverageTimeBetweenClicks(averageTime: Double) {
        val bundle = Bundle().apply {
            putDouble("average_time_between_clicks_seconds", averageTime)
        }
        logEvent("multiplayer_average_time_between_clicks", bundle)
    }
}
