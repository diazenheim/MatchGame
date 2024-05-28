package com.example.matchgame.telemetry
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


class DataCollector(context: Context) {
    private var firebaseAnalytics: FirebaseAnalytics= FirebaseAnalytics.getInstance(context)
    fun trackButton(buttonName: String){
        val bundle= Bundle().apply{
            putString(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            putString("custom_param", "button_click")
        }
        firebaseAnalytics.logEvent("button_click", bundle)
    }
}