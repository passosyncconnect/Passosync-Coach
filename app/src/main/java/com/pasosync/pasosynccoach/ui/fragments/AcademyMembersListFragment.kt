package com.pasosync.pasosynccoach.ui.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.AcademyAdapter
import com.pasosync.pasosynccoach.adapters.AcademyMembersAdapter

import com.pasosync.pasosynccoach.data.UserDetails

import com.pasosync.pasosynccoach.databinding.FragmentAcademyMembersListBinding


private const val TAG = "AcademyMembersListFragment"

class AcademyMembersListFragment : Fragment(R.layout.fragment_academy_members_list) {

    val args: AcademyMembersListFragmentArgs by navArgs()

    private var academyAdapter = AcademyAdapter(listOf())
    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"
    var userList = arrayListOf<UserDetails>()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog


    private lateinit var binding: FragmentAcademyMembersListBinding
    private lateinit var academyMembersAdapter: AcademyMembersAdapter


    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()


    private var counted = ""


    val coachRef = db.collection("UserDetails")


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_academy_members_list, container, false)
        setHasOptionsMenu(true)
        binding = FragmentAcademyMembersListBinding.bind(v)
        //  setUpRecyclerView()
        //   setupRecyclerViewPost()

        return v
    }

    private fun setupRecyclerView() {
        val newcount = getAcademy()



        binding.rvAcademyMember.apply {

            //    Log.d(TAG, "setupRecyclerView: ${getAcademy()}")
            Log.d("args", "argument: ${args.academy}")
            val query = coachRef.whereEqualTo("academy", args.academy)


            val option = FirestoreRecyclerOptions.Builder<UserDetails>()
                    .setQuery(query, UserDetails::class.java)
                    .build()
            academyMembersAdapter = AcademyMembersAdapter(option)

            adapter = academyMembersAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }


    }


    private fun getAcademy(): String {
        var counteded = ""
//        val subscriberCountRef = db.collection("UserDetails")
//                .document(user?.uid!!).get().addOnSuccessListener {
//                    val count = it.get("academy")
//                    // ac_user_list_tv_name.text = count.toString()
//                    // s=count.toString()
//                    counted = count.toString()
//                    counteded = count.toString()
//                    Log.d(TAG, "setupAcademy: $counted")
//
//                }


        coachRef.document(user?.uid!!).addSnapshotListener { value, error ->

            try {
                if (value != null) {
                    val pic = value?.getString("academy")
                    // Glide.with(this).load(pic).placeholder(R.drawable.man).into(profileIvimage)
                    counted = pic!!
                    // Log.d(TAG, "onCreate:$counted")
                } else {
                    //  Log.d(TAG, "onCreate:$counted")
                }
            } catch (e: Exception) {
                //  Log.e(TAG, "onCreate: ${error.toString()}", )
            }

        }


        return counted
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAcademyMembersListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = " "

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        getAcademy()
        setupRecyclerView()
        academyMembersAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("users", it)
            }

            findNavController().navigate(R.id.action_academyMembersListFragment_to_userVideoAndScoreChooserFragment, bundle)
        }

        //    binding.rvAcademyMember.apply {
        //        setHasFixedSize(true)
        //        layoutManager = LinearLayoutManager(requireContext())
        //    }

        //  showData()


    }

    private fun showData() {
        try {
            userList.clear()
            dialog.show()
            db.collection("UserDetails").get().addOnSuccessListener {
                for (document in it.documents) {
                    val userDetails = UserDetails(
                            document.getString("userName")!!,
                            document.getString("userAbout")!!,
                            document.getString("userAge")!!,
                            document.getString("userLevel")!!,
                            document.getString("userPicUri")!!
                    )
                    userList.add(userDetails)
                }
                academyAdapter = AcademyAdapter(userList)
                binding.rvAcademyMember.adapter = academyAdapter
                dialog.dismiss()

            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        academyMembersAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        academyMembersAdapter.stopListening()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}