package com.pasosync.pasosynccoach.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.VideoProgressAdapter
import com.pasosync.pasosynccoach.data.VideoProgressData
import com.pasosync.pasosynccoach.databinding.CoachPlusDashboardBinding
import com.pasosync.pasosynccoach.other.Constant

import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "GameVideoFragment"

class GameVideoFragment : Fragment(R.layout.fragment_game_video) {
    var videoUri: Uri? = null
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var videoProgressAdapter: VideoProgressAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val userGameVideoProgressCollectionRef =
        db.collection("UserDetails").document(user?.uid!!).collection(
            "GameProgressVideoDetails"
        )

    var videoProgressUri: Uri? = null
    var videoProgressUrl: String? = null

    val userVideoProgressStorageReference =
        Firebase.storage.reference.child("UserVideoProgressData")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_game_video, container, false)
        setHasOptionsMenu(true)

        return v
    }

    private fun setUpRecyclerView() = rvGameVideo.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvGameVideo.size}")
        val query = userGameVideoProgressCollectionRef
        val options = FirestoreRecyclerOptions.Builder<VideoProgressData>()
            .setQuery(query, VideoProgressData::class.java)
            .build()
        videoProgressAdapter = VideoProgressAdapter(options)
        adapter = videoProgressAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        // binding = CoachPlusDashboardBinding.bind(view)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()
        videoProgressAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("video", it)
            }
            findNavController().navigate(
                R.id.action_gameVideoFragment_to_videoProgressDetailsAnalytics,
                bundle
            )

        }

        gameVideoFab.setOnClickListener {
            pickVideoIntent()
        }


    }

    override fun onStart() {
        super.onStart()
        videoProgressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        videoProgressAdapter.stopListening()
    }

    private fun pickVideoIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/mp4"
        startActivityForResult(intent, Constant.PICK_VIDEO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_VIDEO && resultCode == Activity.RESULT_OK
        ) {
            videoUri = data?.data

            data?.data?.let {
                videoProgressUri = it

                Toast.makeText(
                    requireContext(),
                    "Your video is Ready to upload",
                    Toast.LENGTH_SHORT
                ).show()

                uploadVideoToStorage()
            }

        }

    }

    private fun uploadVideoToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            videoProgressUri?.let {
                val videoFileName =
                    userVideoProgressStorageReference.child("Video${System.currentTimeMillis()}" + videoProgressUri?.lastPathSegment)
                videoFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        val id = userGameVideoProgressCollectionRef.document().id
                        videoProgressUrl = it.toString()
                        Log.d(TAG, "uploadLectureToStorage: $videoProgressUrl")
                        val videoProgressData = VideoProgressData(videoProgressUrl, "", id)
                        val data =
                            userGameVideoProgressCollectionRef.document(id).set(videoProgressData)
                        Snackbar.make(
                            requireView(),
                            "Uploaded successfully",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        //    findNavController().navigate(R.id.action_fragmentCoachPlusDashBoard_to_gameVideoFragment)


                    }
                }.await()

            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Video Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
//                findNavController().navigate(R.id.action_gameProgressVideoUpload_to_progressVideoFragment)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


}