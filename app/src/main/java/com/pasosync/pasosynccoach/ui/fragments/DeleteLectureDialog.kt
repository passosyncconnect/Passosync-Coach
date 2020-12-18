package com.pasosync.pasosynccoach.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasosync.pasosynccoach.R

class DeleteLectureDialog :DialogFragment(){

    private var yesListener:( () -> Unit)?=null

    fun setYesListener(listener : () -> Unit){
        yesListener=listener
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Delete the Lecture?")
            .setMessage("Are you sure to delete all its data?")
            .setPositiveButton("Yes") { _, _ ->
                yesListener?.let {yes->
                    yes()
                }

            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }.create()




    }

}