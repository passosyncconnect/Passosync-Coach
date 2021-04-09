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
import com.google.firebase.firestore.Query
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.GameScoreAdapter
import com.pasosync.pasosynccoach.data.GameScoreDetails
import kotlinx.android.synthetic.main.fragment_user_score.*


private const val TAG = "UsersScoreFragment"

class UsersScoreFragment:Fragment(R.layout.fragment_user_score) {
val args:UsersScoreFragmentArgs by navArgs()

    lateinit var gameScoreAdapter: GameScoreAdapter
    private val db = FirebaseFirestore.getInstance()
    val user= FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_user_score, container, false)
        setHasOptionsMenu(true)
        // binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }

    private fun setUpRecyclerView() = rvGameScoreCard.apply{
        Log.d(TAG, "setUpRecyclerView: ${rvGameScoreCard.size}")
         val userGameScoreCollectionRef =
                db.collection("UserDetails").document(args.score).collection(
                        "GameScoreCardDetails"
                )
        val query = userGameScoreCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<GameScoreDetails>()
                .setQuery(query, GameScoreDetails::class.java)
                .build()
        gameScoreAdapter = GameScoreAdapter(options)
        adapter = gameScoreAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }







    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.score}")

        setUpRecyclerView()
        gameScoreAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("game", it)
            }
           findNavController().navigate(R.id.action_usersScoreFragment_to_scoreCardDetails,bundle)

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        gameScoreAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        gameScoreAdapter.stopListening()
    }


}