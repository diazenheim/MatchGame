package com.example.matchgame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.matchgame.R
import com.google.firebase.crashlytics.FirebaseCrashlytics

// DialogFragment displays a menu with options to resume or exit the game
class DialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return try{
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return inflater.inflate(R.layout.dialog_menu, container, false)
    } catch(e:Exception) {
        Log.e("DialogFragment","Error creating View",e)
        FirebaseCrashlytics.getInstance().recordException(e)
        null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try{
        super.onViewCreated(view, savedInstanceState)
        val resumeButton: ImageButton = view.findViewById(R.id.resume_button)
        resumeButton.setOnClickListener {
            dismiss()
        }
        val exitButton: ImageButton = view.findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            // Handle choice two
            findNavController().navigate(R.id.action_dialogMenuFragment_to_homeFragment)
            dismiss()
        }

    }catch(e:Exception) {
            Log.e("DialogFragment", "Error on View created", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    override fun onStart() {try{
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        } catch(e:Exception) {
            Log.e("DialogFragment", "Error starting", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
        if (currentFragment is BaseRoundFragment) {
            currentFragment.resumeTimer()
        }
    }
}