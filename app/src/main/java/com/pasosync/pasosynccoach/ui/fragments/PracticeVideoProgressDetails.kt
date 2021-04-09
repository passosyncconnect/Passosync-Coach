package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import kotlinx.android.synthetic.main.practice_video_progress_details_layout.*

class PracticeVideoProgressDetails:Fragment(R.layout.practice_video_progress_details_layout) {
val args:PracticeVideoProgressDetailsArgs by navArgs()
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View =
                inflater.inflate(R.layout.practice_video_progress_details_layout, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaController = MediaController(requireContext())
        val videoUrl = args.video.VideoUrlProgress?.toUri()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        practice_progressVideoDetails.visibility = View.VISIBLE
        practice_progressVideoDetails.setVideoURI(videoUrl)
        practice_progressVideoDetails.setMediaController(mediaController)
        mediaController.setAnchorView(practice_progressVideoDetails)
        mediaController.setMediaPlayer(practice_progressVideoDetails)
        this.practice_progressVideoDetails.setMediaController(mediaController)
        practice_progressVideoDetails.requestFocus()
        practice_progressVideoDetails.start()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}