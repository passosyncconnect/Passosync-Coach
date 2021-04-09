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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SubscribedAdapter
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.fragment_premium_subscribers.*




class PremiumSubscribersFragment : Fragment(R.layout.fragment_premium_subscribers) {

    lateinit var subscribedAdapter: SubscribedAdapter
    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var userList = arrayListOf<UserDetails>()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_premium_subscribers, container, false)
        setHasOptionsMenu(true)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()

        tvSubscriber.setOnClickListener {
            findNavController().navigate(R.id.newPostFragment)
        }

        rvSubs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        showData()
    }

    private fun showData() {
        try {
            userList.clear()
            dialog.show()
            val lectureRef = db.collection("CoachLectureList")
                .document(user?.uid!!).collection("subscribedUserDetails")
                .get().addOnSuccessListener {
                    for (document in it.documents) {

                        val userDetails = UserDetails(
                             document.getString("userName")!!,
                             document.getString("userEmail")!!,
                             document.getString("userMobile")!!,
                            document.getString("userAge")!!,
                            document.getString("userPicUri")!!
                        )
                        userList.add(userDetails)


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
                            db.collection("CoachSubscriberCount").document(user?.uid!!)
                                .set(count)
                    }
                    if (userList.size == 0) {
                        tvSubscriber.visibility = View.VISIBLE
                        dialog.dismiss()

                    } else {
                        //freeShowData()
                        tvSubscriber.visibility = View.GONE
                    }

                    subscribedAdapter = SubscribedAdapter(userList)
                    rvSubs.adapter = subscribedAdapter
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