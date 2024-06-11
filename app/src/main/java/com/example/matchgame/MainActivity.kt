package com.example.matchgame

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.matchgame.telemetry.DataCollector
import com.example.matchgame.ui.HomeFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.navigation.ui.setupActionBarWithNavController

// The main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {
    private lateinit var playModeButton: ImageView
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        DataCollector.initialize(this)


        //try{

            // Initialize Firebase


            // Initialize Firebase Analytics


            //intialize navigation trough nav_graph
            val navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            //setupActionBarWithNavController(navController)


            // Log a test event to verify that events are being logged
            logTestEvent()

            // Initialize DataCollector
            DataCollector.initialize(this)

            // Set user properties
            setUserProperties()

            // Log RAM usage
            DataCollector.logRAMUsage()

        /*} catch (e: Exception) {
            DataCollector.logError("Errore durante l'inizializzazionedell'attività: ${e.message}")
        }*/
    }
    private fun setUserProperties() {
        try {
            // Track device type
            val deviceType = Build.MODEL
            DataCollector.setUserProperty("device_type", deviceType)

            // Track OS version
            val osVersion = Build.VERSION.RELEASE
            DataCollector.setUserProperty("os_version", osVersion)
        }
        catch(e:Exception){
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
        }
        catch(e: Exception){
            DataCollector.logError("Errore durante l'evento di test: ${e.message}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
