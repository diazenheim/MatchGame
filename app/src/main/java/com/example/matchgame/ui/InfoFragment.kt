package com.example.matchgame.ui

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.matchgame.R
import com.example.matchgame.databinding.InfoLayoutBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

// InfoFragment displays information about the application
class InfoFragment : Fragment() {

    private var _binding: InfoLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return try{
        _binding = InfoLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }catch (e:Exception){
            Log.e("InfoFragment","Error creating View",e)
            FirebaseCrashlytics.getInstance().recordException(e)
        null
    }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            binding.infoTextView.text =
                Html.fromHtml(getString(R.string.info_description), Html.FROM_HTML_MODE_LEGACY)
        } catch (e: Exception) {
            Log.e("InfoFragment", "Error on view created", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
