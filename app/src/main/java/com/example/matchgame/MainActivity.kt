package com.example.matchgame

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.matchgame.telemetry.DataCollector
import com.example.matchgame.ui.HomeFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

// The main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {
    private lateinit var playModeButton: ImageView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dataCollector: DataCollector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Log a test event to verify that events are being logged
        logTestEvent()

        // Initialize DataCollector
        dataCollector = DataCollector(this)

        // Set user properties
        setUserProperties()

        // Log RAM usage
        dataCollector.logRAMUsage()


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main_container, HomeFragment()) // Launch HomeFragment
                .commitNow()
        }
    }

    private fun setUserProperties() {
        // Track device type
        val deviceType = Build.MODEL
        dataCollector.setUserProperty("device_type", deviceType)

        // Track OS version
        val osVersion = Build.VERSION.RELEASE
        dataCollector.setUserProperty("os_version", osVersion)
    }

    private fun logTestEvent() {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity")
            putString("custom_param", "test_event")
        }
        firebaseAnalytics.logEvent("test_event", bundle)
    }
}
