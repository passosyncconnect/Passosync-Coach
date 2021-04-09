package com.pasosync.pasosynccoach.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.user_list_item.view.*

class FollowersAdapter(options: FirestoreRecyclerOptions<UserDetails>) :
    FirestoreRecyclerAdapter<UserDetails, FollowersAdapter.FollowersViewHolder>(options) {


    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    inner class FollowersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersViewHolder {
        return FollowersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FollowersViewHolder, position: Int, model: UserDetails) {
        holder.itemView.apply {
            try {

                val subscriberCountRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val count = it.get("userName")
                        user_list_tv_name.text = count.toString()


                    }

                val freeSubscriberCountRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userAge")
                        user_list_tv_age.text = "${freeCount.toString()} yrs"

                    }
                val nameRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val freeCount = it.getString("userAbout")
                        if (freeCount!!.isEmpty()) {
                            user_list_tv_about.text = "nothing about user"
                        } else {
                            user_list_tv_about.text = freeCount.toString()
                        }

                    }

                val aboutRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userKind")
                        user_list_tv_speciality.text = freeCount.toString()

                    }
                val expRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userLevel")

                        user_list_tv_level.text = freeCount.toString()

                    }
                val typeRef = db.collection("UserDetails")
                    .document(model.uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userPicUri")

                        Glide.with(context).load(freeCount).placeholder(R.drawable.man)
                            .into(iv_images)
                    }


            } catch (e: Exception) {
               // Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.d("Adapter", "onBindViewHolder: ${e.message}")
            }


        }


    }


}