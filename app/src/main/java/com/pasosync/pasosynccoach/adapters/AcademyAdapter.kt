package com.pasosync.pasosynccoach.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.academy_user_list_item.view.*

class AcademyAdapter (var mList: List<UserDetails>) :
        RecyclerView.Adapter<AcademyAdapter.AcademyViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    var email = ""

    private val coachCollectRef = db.collection("UserDetails")



    inner class AcademyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcademyViewHolder {
        return AcademyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.academy_user_list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: AcademyViewHolder, position: Int) {
        holder.itemView.apply {
            try {

                val subscriberCountRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val count = it.get("userName")
                            ac_user_list_tv_name.text = count.toString()


                        }

                val freeSubscriberCountRef = db.collection("UserDetails")
                        .document(mList[position].uid).get().addOnSuccessListener {
                            val freeCount = it.getString("userAge")
                            ac_user_list_tv_age.text = "${freeCount.toString()} yrs"
//                            if (freeCount!!.isNotEmpty()){
//                                ac_user_list_tv_age.visibility=View.VISIBLE
//
//                            }else{
//                                ac_user_list_tv_age.visibility=View.GONE
//
//
//                            }


                        }

                val nameRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val freeCount = it.getString("userAbout")
                            ac_user_list_tv_about.text = freeCount.toString()

                        }

                val aboutRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val freeCount = it.get("userKind")
                            ac_user_list_tv_speciality.text = freeCount.toString()

                        }
                val expRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val freeCount = it.get("userLevel")
                            ac_user_list_tv_level.text = freeCount.toString()


                        }
                val typeRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val freeCount = it.get("userPicUri")

                            Glide.with(context).load(freeCount).placeholder(R.drawable.man)
                                    .into(ac_iv_images)
                        }

                val typesRef = db.collection("UserDetails")
                        .document(mList[position].uid!!).get().addOnSuccessListener {
                            val freeCount = it.getString("userType")

//                            Glide.with(context).load(freeCount).placeholder(R.drawable.man)
//                                    .into(iv_images)
                            if (freeCount == "coach") {
                                ac_user_list_tag.text = "Coach"

                            } else {
                                ac_user_list_tag.text = "User"

                            }

                        }


            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.d("Adapter", "onBindViewHolder: ${e.message}")
            }


        }


    }

    override fun getItemCount(): Int {
     return mList.size
    }


}