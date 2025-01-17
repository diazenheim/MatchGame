package com.example.matchgame

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.matchgame.telemetry.DataCollector
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.provider.Settings

import com.example.matchgame.ui.BaseRoundFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics

// The main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {


    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var navController: NavController
    private var initialBatteryPercentage: Int = 0
    private var isGameCompleted: Boolean = false // Flag to track game completion
    private var currentRound: Int = 0 // Variable to track current round

    // Companion object to store the app start time and permission request code
    companion object {
        var appStartTime: Long = System.currentTimeMillis()
        private const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            setContentView(R.layout.activity_main)

            // Initialize Firebase Analytics and Crashlytics
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

            // Initialize DataCollector
            DataCollector.initialize(this)

            // Capture the initial battery percentage
            initialBatteryPercentage = DataCollector.getBatteryPercentage(this)
            Log.d("MainActivity", "Initial battery percentage: $initialBatteryPercentage%")


            // Initialize navigation through nav_graph
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            // Request necessary permissions
            checkAndRequestPermissions()

            // Set user properties
            setUserProperties()

            // Log RAM usage
            DataCollector.logRAMUsage()

            // Log the time taken to launch the app
            val launchTime = (System.currentTimeMillis() - appStartTime) / 1000.0
            DataCollector.logAppLaunchTime(launchTime)

            // Set a default uncaught exception handler to log exceptions to Firebase Crashlytics
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Set user properties for device type and OS version
    private fun setUserProperties() {
        try {
            // Track device type
            val deviceType = Build.MODEL
            DataCollector.setUserProperty("device_type", deviceType)

            // Track OS version
            val osVersion = Build.VERSION.RELEASE
            DataCollector.setUserProperty("os_version", osVersion)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting the user properties", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Handle navigation up action
    override fun onSupportNavigateUp(): Boolean {
        return try {
            return navController.navigateUp() || super.onSupportNavigateUp()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error on onSupportNavigateUp", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }


    // Log battery usage difference when the app stops
    override fun onStop() {
        super.onStop()
        try {
            Log.d("MainActivity", "onStop called")
            val endBatteryPercentage = DataCollector.getBatteryPercentage(this)
            Log.d("MainActivity", "End battery percentage: $endBatteryPercentage%")
            DataCollector.logBatteryUsage(initialBatteryPercentage, endBatteryPercentage)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error on stopping", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Call this method when the game is completed
    fun onGameCompleted() {
        isGameCompleted = true
    }

    // Call this method to update the current round
    fun updateCurrentRound(round: Int) {
        currentRound = round
    }

    // Check and request necessary permissions
    private fun checkAndRequestPermissions() {
        try {
            val permissionsNeeded = mutableListOf<String>()
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
            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking the permission", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Handle the result of permission requests
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MainActivityPErmission", "Permesso richiesto, godo")
                        // Permission was not granted, show a dialog explaining why it is needed
                        when (permissions[i]) {
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
        } catch (e: Exception) {
            Log.e("MainActivity", "Error on result of checking permissions", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Show a dialog explaining why the permission is needed
    private fun showPermissionDeniedDialog(message: String) {
        try {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Settings") { dialog, _ ->
                    dialog.dismiss()
                    // Open app settings to allow the user to enable the permission manually
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing denied permission", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    // Handle back button press
    override fun onBackPressed() {
        try {
            // Check which fragment is currently visible
            val currentDestination = navController.currentDestination?.id
            if (currentDestination == R.id.multiplayerFragment) navController.navigate(R.id.dialogMenuFragment)
            else if (currentDestination == R.id.round1Fragment || currentDestination == R.id.round2Fragment || currentDestination == R.id.round3Fragment) {
                // Navigate to the DialogFragment when the back button is pressed
                navController.navigate(R.id.dialogMenuFragment)
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(
                        0
                    )
                if (currentFragment is BaseRoundFragment) {
                    currentFragment.pauseTimer()
                }
            } else if (currentDestination == R.id.youWinFragment || currentDestination == R.id.youLoseFragment
                || currentDestination == R.id.player1WinFragment || currentDestination == R.id.player2WinFragment
            ) {
                navController.navigate(R.id.homeFragment)
            } else if (currentDestination == R.id.homeFragment) finishAffinity()
            else {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error onBackPressed", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}


