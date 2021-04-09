package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.LectureContentBinding


class LectureContent : Fragment(R.layout.lecture_content) {

   private lateinit var binding:LectureContentBinding
    private val TAG = "LectureContent"
    lateinit var mediaController: MediaController
    val args: LectureContentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.lecture_content, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= LectureContentBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = " "
        mediaController = MediaController(requireContext())

        val lecture = args.lecture
        binding.lectureDetailsTvName.text = lecture.lectureName
        binding.lectureDetailsTvDescription.text = lecture.lectureContent
        binding.lectureDetailsTvDescription.movementMethod = ScrollingMovementMethod()
        binding.lectureDetailsTvType.text=lecture.seacrh
        val videoUrl = lecture.lectureVideoUrl?.toUri()
        Log.d(TAG, "onViewCreated: ${lecture.lecturePdfUrl}")

        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            binding.videoLecture.visibility=View.VISIBLE
            binding.videoLecture.setVideoURI(videoUrl)
            binding.videoLecture.setMediaController(mediaController)
            mediaController.setAnchorView(binding.videoLecture)
            mediaController.setMediaPlayer(binding.videoLecture)
            this.binding.videoLecture.setMediaController(mediaController)
            binding.videoLecture.requestFocus()
            binding.videoLecture.start()
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}