package com.pasosync.pasosynccoach.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.pasosync.pasosynccoach.R
import androidx.fragment.app.DialogFragment
import com.pasosync.pasosynccoach.adapters.CommentAdapter
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import com.pasosync.pasosynccoach.other.Event
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_comment.*

private const val TAG = "CommentDialog"

class CommentDialog :DialogFragment() {

private lateinit var viewModel: MainViewModels
    private val args:CommentDialogArgs by navArgs()


    private val commentAdapter = CommentAdapter()

    private lateinit var dialogView:View
    private lateinit var rvComments: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnComment: Button
    private lateinit var commentProgressBar: ProgressBar


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_comment,
            null
        )
        rvComments = dialogView.findViewById(R.id.rvComments)
        etComment = dialogView.findViewById(R.id.etComment)
        btnComment = dialogView.findViewById(R.id.btnComment)
        commentProgressBar = dialogView.findViewById(R.id.commentProgressBar)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comments)
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
        setupRecyclerView()
        subscribeToObservers()

       // Log.d(TAG, "onViewCreated: ${args.postId}")

      viewModel.getCommentsForPost(args.postId)
        btnComment.setOnClickListener {
            val commentText = etComment.text.toString()
            viewModel.createComment(commentText, args.postId)
            etComment.text?.clear()
        }


        commentAdapter.setOnDeleteCommentClickListener { comment ->
            viewModel.deleteComment(comment)
        }

        commentAdapter.setOnUserClickListener { comment ->
            if (FirebaseAuth.getInstance().uid!! == comment.uid) {
                requireActivity().bottomNavigationView.selectedItemId = R.id.profileFragment2
                return@setOnUserClickListener
            }
//            findNavController().navigate(
//                CommentDialogDirections.globalActionToOthersProfileFragment(comment.uid)
//            )

        }


    }


    private fun subscribeToObservers() {
        viewModel.commentsForPost.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                Log.d(TAG, "subscribeToObservers: $it")
                // snackbar(it)
                 Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            },
            onLoading = { commentProgressBar.isVisible = true }
        ) { comments ->
            commentProgressBar.isVisible = false
            commentAdapter.comments = comments
        })
        viewModel.createCommentStatus.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.d(TAG, "subscribeToObservers: $it")
                commentProgressBar.isVisible = false
                //  snackbar(it)
                btnComment.isEnabled = true
            },
            onLoading = {
                commentProgressBar.isVisible = true
                btnComment.isEnabled = false
            }
        ) { comment ->
            commentProgressBar.isVisible = false
            btnComment.isEnabled = true
            commentAdapter.comments += comment
        })
        viewModel.deleteCommentStatus.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                Log.d(TAG, "subscribeToObservers: $it")
                //  snackbar(it)
            },
            onLoading = { commentProgressBar.isVisible = true }
        ) { comment ->
            commentProgressBar.isVisible = false
            commentAdapter.comments -= comment

        })
    }


    private fun setupRecyclerView() = rvComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

}