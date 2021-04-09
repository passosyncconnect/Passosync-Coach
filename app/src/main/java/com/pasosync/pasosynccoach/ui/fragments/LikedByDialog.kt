package com.pasosync.pasosynccoach.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.auth.User
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SubscribedAdapter
import com.pasosync.pasosynccoach.adapters.UserAdapter

class LikedByDialog(
    private val subscribedAdapter: UserAdapter
):DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rvLikedBy=RecyclerView(requireContext()).apply {
            adapter=subscribedAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }
return MaterialAlertDialogBuilder(requireContext())
    .setTitle(R.string.liked_by_dialog_title).setView(rvLikedBy)
    .create()
    }



}