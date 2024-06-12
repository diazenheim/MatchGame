package com.example.matchgame

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.matchgame.telemetry.DataCollector
import com.example.matchgame.ui.HomeFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// The main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var navController: NavController
    private var initialBatteryPercentage: Int = 0
    private var isGameCompleted: Boolean = false // Flag to track game completion
    private var currentRound: Int = 0 // Variable to track current round

    //private var isPotentiallyClosing: Boolean = false // Flag to track potential closing


    // Companion object to store the app start time
    companion object {
        var appStartTime: Long = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Initialize DataCollector
        DataCollector.initialize(this)

        // Capture the initial battery percentage
        initialBatteryPercentage = DataCollector.getBatteryPercentage(this)
        Log.d("MainActivity", "Initial battery percentage: $initialBatteryPercentage%")


        //intialize navigation trough nav_graph
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        // Log a test event to verify that events are being logged
        //logTestEvent()


        // Set user properties
        setUserProperties()

        // Log RAM usage
        DataCollector.logRAMUsage()

        // Log the time taken to launch the app
        val launchTime = (System.currentTimeMillis() - appStartTime) / 1000.0
        DataCollector.logAppLaunchTime(launchTime)

    }

    private fun setUserProperties() {
        try {
            // Track device type
            val deviceType = Build.MODEL
            DataCollector.setUserProperty("device_type", deviceType)

            // Track OS version
            val osVersion = Build.VERSION.RELEASE
            DataCollector.setUserProperty("os_version", osVersion)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante l'impostazione dell'utente: ${e.message}")
        }
    }

    private fun logTestEvent() {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity")
                putString("custom_param", "test_event")
            }
            firebaseAnalytics.logEvent("test_event", bundle)
        } catch (e: Exception) {
            DataCollector.logError("Errore durante l'evento di test: ${e.message}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp() || super.onSupportNavigateUp()
    }



    // Add a method to log battery usage difference
    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
        val endBatteryPercentage = DataCollector.getBatteryPercentage(this)
        Log.d("MainActivity", "End battery percentage: $endBatteryPercentage%")
        DataCollector.logBatteryUsage(initialBatteryPercentage, endBatteryPercentage)
    }



    // Call this method when the game is completed
    fun onGameCompleted() {
        isGameCompleted = true
    }

    // Call this method to update the current round
    fun updateCurrentRound(round: Int) {
        currentRound = round
    }

}
