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
import com.pasosync.pasosynccoach.adapters.PracticeVideoProgressAdapter
import com.pasosync.pasosynccoach.data.PracticeVideoProgressData
import kotlinx.android.synthetic.main.fragment_user_practice_video.*


private const val TAG = "UserPracticeVideoFragme"
class UserPracticeVideoFragment:Fragment(R.layout.fragment_user_practice_video) {
val args:UserPracticeVideoFragmentArgs by navArgs()

    lateinit var videoProgressAdapter: PracticeVideoProgressAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_user_practice_video, container, false)
        setHasOptionsMenu(true)
        // binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }

    private fun setUpRecyclerView() = rvPracticeVideo.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvPracticeVideo.size}")
       val userPracticeVideoProgressectionRef =
                db.collection("UserDetails").document(args.practice).collection(
                        "PracticeProgressVideoDetails"
                )
        val query = userPracticeVideoProgressectionRef
        val options = FirestoreRecyclerOptions.Builder<PracticeVideoProgressData>()
                .setQuery(query, PracticeVideoProgressData::class.java)
                .build()
        videoProgressAdapter = PracticeVideoProgressAdapter(options)
        adapter = videoProgressAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.practice}")
        setUpRecyclerView()

        videoProgressAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("video",it)
            }
            findNavController().navigate(R.id.action_userPracticeVideoFragment_to_practiceVideoProgressDetails,bundle)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}