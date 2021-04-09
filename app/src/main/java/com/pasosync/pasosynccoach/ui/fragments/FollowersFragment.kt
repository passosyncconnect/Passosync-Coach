package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.CoachListAdapter
import com.pasosync.pasosynccoach.adapters.FollowersAdapter
import com.pasosync.pasosynccoach.adapters.SubscribedAdapter
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.fragment_premium_subscribers.*


private const val TAG = "FollowersFragment"

class FollowersFragment : Fragment(R.layout.fragment_followers) {
    // lateinit var subscribedAdapter: SubscribedAdapter
    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var userList = arrayListOf<UserDetails>()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var followersAdapter: FollowersAdapter
    val coachRef = db.collection("CoachLectureList")
                .document(user?.uid!!).collection("FreeSubscribedUsers")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_followers, container, false)
        setHasOptionsMenu(true)
        return v
    }


    private fun setupRecyclerView() {
        rvSubsFree.apply {
            val query = coachRef
            val option = FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails::class.java)
                .build()
            followersAdapter = FollowersAdapter(option)
            adapter = followersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()




        tvFollowers.setOnClickListener {
            findNavController().navigate(R.id.newPostUploadFragment)
        }

        //  freeShowData()

    }


//    private fun freeShowData() {
//        try {
//            dialog.show()
//            userList.clear()
//            val freelectureRef = db.collection("CoachLectureList")
//                .document(user?.uid!!).collection("FreeSubscribedUsers")
//                .get().addOnSuccessListener {
//                    for (document in it.documents) {
//                        dialog.dismiss()
//                        val userDetails = UserDetails(
//                            userName = document.getString("userName")!!,
//                          userAbout =   document.getString("userAbout")!!,
//                          userKind =   document.getString("userKind")!!,
//                          userAge =   document.getString("userAge")!!,
//                           userPicUri =  document.getString("userPicUri")!!,
//                           userLevel =  document.getString("userLevel")!!
//                        )
//                        userList.add(userDetails)
//
//                        val count: HashMap<String, Any> = HashMap()
//                        count[KEY_FREE] = userList.size
//                        val countRef =
//                            db.collection("FreeCoachSubscriberCount")
//                                .document(user?.uid!!)
//                                .set(count)
//                    }
//                    if (userList.size == 0) {
//                        tvFollowers.visibility = View.VISIBLE
//                        dialog.dismiss()
//
//                    } else {
//                        //freeShowData()
//                        tvFollowers.visibility = View.GONE
//                    }
//
//
//                    subscribedAdapter = SubscribedAdapter(userList)
//                    rvSubsFree.adapter = subscribedAdapter
//                    dialog.dismiss()
//
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    dialog.dismiss()
//
//                }
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//            Log.d(TAG, "freeShowData: ${e.message}", )
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        followersAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        followersAdapter.stopListening()
    }
}