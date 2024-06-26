package com.example.matchgame

import android.Manifest
import android.content.Intent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.provider.Settings


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
        private const val PERMISSION_REQUEST_CODE = 1
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


        //Ask permission
        checkAndRequestPermissions()

        // Set user properties
        setUserProperties()

        // Log RAM usage
        DataCollector.logRAMUsage()

        // Log the time taken to launch the app
        val launchTime = (System.currentTimeMillis() - appStartTime) / 1000.0
        DataCollector.logAppLaunchTime(launchTime)
        //DataCollector.logInternNetworkState()
        //DataCollector.logInternetVelocity()

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

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        /*if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
         if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

         */

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.INTERNET)
        }


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Aggiungi altri permessi se necessario

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivityPErmission", "Permesso richiesto, godo")
                    // Il permesso non è stato concesso, gestisci di conseguenza
                    when (permissions[i]) {
                        /*Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            showPermissionDeniedDialog("Write External Storage permission is required to save game data. Please enable it in settings.")
                        }

                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            showPermissionDeniedDialog("Read External Storage permission is required to read game data. Please enable it in settings.")
                        }*/

                        Manifest.permission.POST_NOTIFICATIONS -> {
                            showPermissionDeniedDialog("Notification permission is required to receive notifications. Please enable it in settings.")
                        }

                        Manifest.permission.INTERNET -> {
                            showPermissionDeniedDialog("Notification permission is required to receive notifications. Please enable it in settings.")
                        }

                        Manifest.permission.ACCESS_NETWORK_STATE -> {
                            showPermissionDeniedDialog("Notification permission is required to receive notifications. Please enable it in settings.")
                        }
                    }
                }
            }
        }
    }
    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Settings") { dialog, _ ->
                dialog.dismiss()
                // Apri le impostazioni dell'app per permettere all'utente di concedere i permessi manualmente
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    override fun onBackPressed() {
        // Controlla quale fragment è attualmente visibile
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.round1Fragment || currentDestination == R.id.round2Fragment || currentDestination == R.id.round3Fragment || currentDestination == R.id.multiplayerFragment) {
            // Naviga al DialogFragment quando il tasto "indietro" è premuto
            navController.navigate(R.id.dialogMenuFragment)
        }
        else if(currentDestination == R.id.youWinFragment || currentDestination == R.id.youLoseFragment
                || currentDestination == R.id.player1WinFragment || currentDestination == R.id.player2WinFragment)
        {
            navController.navigate(R.id.homeFragment)
        }
        else {
            super.onBackPressed()
        }
    }
}


