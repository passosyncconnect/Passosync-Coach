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
import kotlinx.android.synthetic.main.video_progress_details_layout.*




class VideoProgressDetailsUsers:Fragment(R.layout.video_progress_details_layout) {
val args:VideoProgressDetailsUsersArgs by navArgs()
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.video_progress_details_layout, container, false)
        setHasOptionsMenu(true)
        // binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaController = MediaController(requireContext())
        val videoUrl = args.videos.VideoUrlProgress?.toUri()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        progressVideoDetails.visibility = View.VISIBLE
        progressVideoDetails.setVideoURI(videoUrl)
        progressVideoDetails.setMediaController(mediaController)
        mediaController.setAnchorView(progressVideoDetails)
        mediaController.setMediaPlayer(progressVideoDetails)
        this.progressVideoDetails.setMediaController(mediaController)
        progressVideoDetails.requestFocus()
        progressVideoDetails.start()
       // showData()

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}