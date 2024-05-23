package com.example.matchgame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.matchgame.ui.UiFragment

//the main entry point of the app, responsible for loading the fragment that contains the game UI
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UiFragment())
                .commitNow()
        }
    }
}
