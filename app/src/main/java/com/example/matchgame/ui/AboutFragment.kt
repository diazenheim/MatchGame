package com.example.matchgame.ui

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.databinding.AboutLayoutBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AboutFragment : Fragment() {

    private var _binding: AboutLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return try{
        _binding = AboutLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }catch(e:Exception){
        Log.e("AboutFragment", "Error inflating view", e)
        FirebaseCrashlytics.getInstance().recordException(e)
        null

    }    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try{
        super.onViewCreated(view, savedInstanceState)
        binding.aboutTextView.text = Html.fromHtml(getString(R.string.about_description), Html.FROM_HTML_MODE_LEGACY)
    }catch(e:Exception){
            Log.e("AboutFragment", "Error setting about description", e)
            FirebaseCrashlytics.getInstance().recordException(e)

        }    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
