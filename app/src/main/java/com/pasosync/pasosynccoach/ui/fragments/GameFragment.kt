package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.GameScoreAdapter
import com.pasosync.pasosynccoach.data.GameScoreDetails

import kotlinx.android.synthetic.main.fragment_game.*

private const val TAG = "GameFragment"
class GameFragment : Fragment(R.layout.fragment_game) {
    lateinit var gameScoreAdapter: GameScoreAdapter
    private val db = FirebaseFirestore.getInstance()
    val user=FirebaseAuth.getInstance().currentUser
    private val userGameScoreCollectionRef =
        db.collection("UserDetails").document(user?.uid!!).collection(
            "GameScoreCardDetails"
        )


    private fun setUpRecyclerView() = rvScoreCard.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvScoreCard.size}")
        val query = userGameScoreCollectionRef.orderBy("timestamp",Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<GameScoreDetails>()
            .setQuery(query, GameScoreDetails::class.java)
            .build()
        gameScoreAdapter = GameScoreAdapter(options)
        adapter = gameScoreAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onStart() {
        super.onStart()
        gameScoreAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        gameScoreAdapter.stopListening()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = " "
        setUpRecyclerView()

        gameScoreAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("score", it)
            }
//            findNavController().navigate(
//                R.id.action_yourProgressFragment_to_scoreCardDetails2,
//                bundle
//            )


        }



//        gameVideoFab.setOnClickListener {
//
//            findNavController().navigate(R.id.action_yourProgressFragment_to_gameProgressVideoUpload)
//        }
    }


}