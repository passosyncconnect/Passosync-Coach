package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.VideoProgressAdapter
import com.pasosync.pasosynccoach.data.VideoProgressData
import com.pasosync.pasosynccoach.databinding.FragmentUserVideoAndScoreChooserBinding
import kotlinx.android.synthetic.main.fragment_user_video.*


private const val TAG = "UsersVideoFragment"
class UsersVideoFragment:Fragment(R.layout.fragment_user_video) {
val args:UsersVideoFragmentArgs by navArgs()
    lateinit var videoProgressAdapter: VideoProgressAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_user_video, container, false)
        setHasOptionsMenu(true)
       // binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }

    private fun setUpRecyclerView() = rvGameVideo.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvGameVideo.size}")
        val userGameVideoProgressCollectionRef =
                db.collection("UserDetails").document(args.video).collection(
                        "GameProgressVideoDetails"
                )
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

        Log.d(TAG, "onViewCreated: ${args.video}")
        setUpRecyclerView()
        videoProgressAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("videos",it)
            }
            findNavController().navigate(R.id.action_usersVideoFragment_to_videoProgressDetails,bundle)

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        videoProgressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        videoProgressAdapter.stopListening()
    }


}