package com.pasosync.pasosynccoach.ui.fragments

import android.graphics.Color
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
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SubscribedAdapter
import com.pasosync.pasosynccoach.data.SubscribedUserEmails
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.databinding.FragmentSubscribersBinding


class SubscribersFragment : Fragment(R.layout.fragment_subscribers) {
    private lateinit var binding: FragmentSubscribersBinding
    private val TAG = "SubscribersFragment"
    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"

    //    lateinit var lectureAdapter: LectureAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var userList = arrayListOf<UserDetails>()
    var emailList= arrayListOf<String>()
    lateinit var subscribedAdapter: SubscribedAdapter
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    val userDetailsToCoach = db.collection("CoachLectureList")
        .document(user?.email.toString()).collection("subscribedUserDetails")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_subscribers, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentSubscribersBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.title = " "

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        binding.rvSubs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        showData()

        binding.freeSubs.setOnClickListener {
            binding.freeSubs.isSelected = !binding.freeSubs.isSelected
            binding.paidSubs.isSelected = false
            freeShowData()
        }
        binding.paidSubs.setOnClickListener {
            binding.paidSubs.isSelected = !binding.paidSubs.isSelected
            binding.freeSubs.isSelected = false
            showData()
        }


    }

    private fun showData() {
        try {
            userList.clear()
            dialog.show()
            val lectureRef = db.collection("CoachLectureList")
                .document(user?.email.toString()).collection("subscribedUserDetails")
                .get().addOnSuccessListener {
                    for (document in it.documents) {

                        val userDetails = UserDetails(
                            document.getString("userName"),
                            document.getString("userEmail"),
                            document.getString("userMobile"),
                            document.getString("userAge"),
                            document.getString("userPicUri")
                        )
                        userList.add(userDetails)
                        Log.d(TAG, "showData: ${userList.size}")

//                        try {
//
//                            for (index in 0..userList.size){
//                                Log.d(TAG, "showlist:${userList[index].userEmail} ")
//                                val subscribedUserEmails=SubscribedUserEmails(
//                                    userList[index].userEmail.toString()
//                                )
//                                //emailList.add(subscribedUserEmails)
//                                emailList.add(userList[index].userEmail.toString())
////                                val SubscriberEmailRef=db.collection("CoachLectureList")
////                                    .document(user?.email.toString()).collection("subscriberEmailList")
////                                SubscriberEmailRef.add(subscribedUserEmails)
//                            }
//                            Log.d(TAG, "showData: ${emailList}")
//                            Log.d(TAG, "showData: ${emailList.size}")
//
//
//                        } catch (e: Exception) {
//                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//                        }

                        val count: HashMap<String, Any> = HashMap()
                        count[KEY_TITLE] = userList.size
                        val countRef =
                            db.collection("CoachSubscriberCount").document(user?.email.toString())
                                .set(count)
                    }
                    subscribedAdapter = SubscribedAdapter(userList)
                    binding.rvSubs.adapter = subscribedAdapter
                    dialog.dismiss()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun freeShowData() {
        try {
            dialog.show()
            userList.clear()
            val freelectureRef = db.collection("CoachLectureList")
                .document(user?.email.toString()).collection("FreeSubscribedUsers")
                .get().addOnSuccessListener {
                    for (document in it.documents) {
                        dialog.dismiss()
                        val userDetails = UserDetails(
                            document.getString("userName"),
                            document.getString("userEmail"),
                            document.getString("userMobile"),
                            document.getString("userAge"),
                            document.getString("userPicUri")
                        )
                        userList.add(userDetails)
                        Log.d(TAG, "showData: ${userList.size}")
                        val count: HashMap<String, Any> = HashMap()
                        count[KEY_FREE] = userList.size
                        val countRef =
                            db.collection("FreeCoachSubscriberCount")
                                .document(user?.email.toString())
                                .set(count)
                    }
                    subscribedAdapter = SubscribedAdapter(userList)
                    binding.rvSubs.adapter = subscribedAdapter
                    dialog.dismiss()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}