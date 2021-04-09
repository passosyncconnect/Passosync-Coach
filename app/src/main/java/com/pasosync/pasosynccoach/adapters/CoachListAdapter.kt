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
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.item_all_coach_list.view.*


private const val TAG = "CoachListAdapter"
class CoachListAdapter(options: FirestoreRecyclerOptions<UserDetails>) :
    FirestoreRecyclerAdapter<UserDetails, CoachListAdapter.CoachListViewHolder>(options) {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    var email = ""
    var free_count: Long = 0
    private val coachCollectRef = db.collection("UserDetails")
    var isConnected=false


    inner class CoachListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((UserDetails) -> Unit)? = null
    private var onFollowItemClickListener: ((UserDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (UserDetails) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnFollowClickListener(listener: (UserDetails) -> Unit) {
        onFollowItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachListViewHolder {
        return CoachListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_all_coach_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CoachListViewHolder,
        position: Int,
        model: UserDetails
    ) {
        holder.itemView.apply {
            try {

                db.collection("CoachSubscriberCount")
                    .document(model.uid).get().addOnSuccessListener {
                        val count = it.get("title")
                        subscriber_count.text = count.toString()


                    }



//                db.runTransaction { transaction->
//                    val currentUser =
//                        transaction.get(coachCollectRef.document(user))
//                            .toObject(CoachDetails::class.java)!!
//                    isConnected=model.coachEmail in currentUser.follows
//                    Log.d(TAG, "onBindViewHolder: $isConnected")
//                    if (isConnected){
//                        Log.d(TAG, "true:$isConnected")
//                        tvConnected.visibility=View.VISIBLE
//                    }else{
//                        tvConnected.visibility=View.GONE
//                        Log.d(TAG, "false:$isConnected ")
//                    }
//
//
//                }


                coachCollectRef.document(user?.uid!!).get().addOnSuccessListener {
                    val coach=it.toObject(UserDetails::class.java)!!

                    isConnected=model.uid in coach.followsCoaches
                    if (isConnected){
                        Log.d(TAG, "true:$isConnected")
                        tvConnected.visibility=View.VISIBLE

                    }else{
                        tvConnected.visibility=View.GONE
                        Log.d(TAG, "false:$isConnected ")
                    }

                }





                val freeSubscriberCountRef = db.collection("FreeCoachSubscriberCount")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("free")
                        tv_follower_count.text = freeCount.toString()
                    }
                val coachFollowerCountRef = db.collection("CoachFollowersCount")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("followers")
                        list_coach_connected.text = freeCount.toString()

                    }

                val nameRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userName")
                        coach_list_tv_name.text = freeCount.toString()

                    }
                val verifyRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.getBoolean("verify")!!
                        if (freeCount) {
                            verfiy_linear.visibility = View.VISIBLE
                        } else {
                            verfiy_linear.visibility = View.INVISIBLE
                        }



                    }

                val aboutRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userAbout")
                        coach_list_tv_description.text = freeCount.toString()

                    }

                val levelRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userAbout")
                        coach_list_tv_description.text = freeCount.toString()

                    }


                val expRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userLevel").toString()
                        if(freeCount.isEmpty()){
                            tv_level_coach_list.visibility=View.GONE
                        }else{
                            tv_level_coach_list.text = freeCount.toString()
                            tv_level_coach_list.visibility=View.VISIBLE
                        }




                    }
                val typeRef = db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userKind")
                        tv_speciality_coach_list.text = freeCount.toString()

                    }



                db.collection("UserDetails")
                    .document(model.uid).get().addOnSuccessListener {
                        val freeCount = it.get("userPicUri")
                        Glide.with(context).load(freeCount).placeholder(R.drawable.man).centerCrop()
                            .into(iv_images_coach)
                    }



            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }




            setOnClickListener {
                if (model.uid == user?.uid!!) {
                    Toast.makeText(context, "You can't follow yourself", Toast.LENGTH_SHORT).show()
                } else {
                    onItemClickListener?.let {
                        it(model)
                    }

                }

            }

        }
    }











}