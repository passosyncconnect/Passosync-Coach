package com.pasosync.pasosynccoach.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.UserAdapter
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import com.pasosync.pasosynccoach.other.Event

private const val TAG = "LikedDialog"
class LikedDialog:DialogFragment() {
    private lateinit var viewModel: MainViewModels

    private val args:LikedDialogArgs by navArgs()

    private var userAdapter=UserAdapter()
    private lateinit var dialogView: View
    private lateinit var rvLikedBy: RecyclerView
    private lateinit var likeProgressBar: ProgressBar

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_liked,
            null
        )
        rvLikedBy = dialogView.findViewById(R.id.rvLikedUsers)
        likeProgressBar = dialogView.findViewById(R.id.likeProgressBar)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.liked_by_dialog_title)
            .setView(dialogView)
            .create()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewmodel
       // Log.d(TAG, "onViewCreated: ${args.id.likedBy}")
        subscribeToObservers()
        setupRecyclerView()

        viewModel.getUsers(args.id.likedBy)

    }

    private fun subscribeToObservers() {
        viewModel.likedByUsers.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                //snackbar(it)
                // binding.progressBarPost.isVisible = false
                likeProgressBar.isVisible = false
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.d("TAG", "subscribeToObservers: $it")
            }, onLoading = {
                likeProgressBar.isVisible = true
            }

        ) { users ->
            likeProgressBar.isVisible = false

            userAdapter.users = users
            Log.d(TAG, "subscribeToObservers: call")
            //  LikedByDialog(userAdapter).show(childFragmentManager, null)
        }

        )


    }


    private fun setupRecyclerView() = rvLikedBy.apply {
        adapter = userAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}