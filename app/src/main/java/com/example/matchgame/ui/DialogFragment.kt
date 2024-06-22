package com.example.matchgame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.matchgame.R

class MenuDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.choice_one).setOnClickListener {
            // Handle choice one
            dismiss()
        }

        view.findViewById<Button>(R.id.choice_two).setOnClickListener {
            // Handle choice two
            dismiss()
        }

        view.findViewById<Button>(R.id.choice_three).setOnClickListener {
            // Handle choice three
            dismiss()
        }
    }
}