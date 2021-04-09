package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.CoachListAdapter
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.databinding.FargmentDashboardBinding
import com.pasosync.pasosynccoach.databinding.FragmentAllCoachListBinding

class AllCoachListFragment : Fragment(R.layout.fragment_all_coach_list) {
    lateinit var binding: FragmentAllCoachListBinding
    lateinit var coachListAdapter: CoachListAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    val coachRef = db.collection("UserDetails")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_all_coach_list, container, false)
        setHasOptionsMenu(true)
        binding = FragmentAllCoachListBinding.bind(v)
        //  setUpRecyclerView()

        return v
    }

    private fun setupRecyclerView() {
        binding.rvAllCoachesList.apply {
            val query = coachRef.whereEqualTo("userType","coach")
            val option = FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails::class.java)
                .build()
            coachListAdapter = CoachListAdapter(option)
            adapter = coachListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllCoachListBinding.bind(view)
        setupRecyclerView()

        coachListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("coach", it)
            }
            findNavController().navigate(
                R.id.action_allCoachListFragment_to_coachDescriptionFragment,
                bundle
            )
        }


    }

    override fun onStart() {
        super.onStart()
        coachListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        coachListAdapter.stopListening()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}