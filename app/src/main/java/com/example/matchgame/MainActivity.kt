package com.example.matchgame

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchgame.ui.HomeFragment
import com.example.matchgame.ui.UiFragment
import com.google.firebase.analytics.FirebaseAnalytics


//the main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {
    private lateinit var playModeButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)
        FirebaseAnalytics.getInstance(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main_container, HomeFragment()) //lancia HomeFragment
                .commitNow()
        }
    }
}
