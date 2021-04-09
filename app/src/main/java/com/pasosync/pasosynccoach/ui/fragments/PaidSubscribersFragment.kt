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
import com.pasosync.pasosynccoach.databinding.FragmentPaidSubscribersBinding



class PaidSubscribersFragment:Fragment(R.layout.fragment_paid_subscribers) {
    private lateinit var binding: FragmentPaidSubscribersBinding

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var userList = arrayListOf<UserDetails>()
    lateinit var subscribedAdapter: SubscribedAdapter
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private val KEY_TITLE = "title"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_paid_subscribers, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentPaidSubscribersBinding.bind(view)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        binding.rvPaidSubscriberFragment.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        showData()


    }

    private fun showData() {
        try {
            dialog.show()
            userList.clear()
            val freelectureRef = db.collection("CoachLectureList")
                .document(user?.uid!!).collection("subscribedUserDetails")
                .get().addOnSuccessListener {
                    for (document in it.documents) {
                        dialog.dismiss()
                        val userDetails = UserDetails(
                            document.getString("userName")!!,
                            document.getString("userEmail")!!,
                            document.getString("userMobile")!!,
                            document.getString("userAge")!!,
                            document.getString("userPicUri")!!
                        )
                        userList.add(userDetails)

                        val count: HashMap<String, Any> = HashMap()
                        count[KEY_TITLE] = userList.size
                        val countRef =
                            db.collection("CoachSubscriberCount")
                                .document(user?.uid!!)
                                .set(count)
                    }
                    subscribedAdapter = SubscribedAdapter(userList)
                    binding.rvPaidSubscriberFragment.adapter = subscribedAdapter
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