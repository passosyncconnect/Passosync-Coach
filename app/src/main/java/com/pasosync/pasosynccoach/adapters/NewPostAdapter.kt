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
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.data.Post
import kotlinx.android.synthetic.main.item_post.view.*

class NewPostAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, NewPostAdapter.PostNewViewHolder>(options) {

    private val db = FirebaseFirestore.getInstance()

    inner class PostNewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostNewViewHolder {
        return PostNewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post,parent,false
            )
        )


    }

    override fun onBindViewHolder(holder: PostNewViewHolder, position: Int, model: Post) {
holder.itemView.apply {
    Glide.with(context).load(model.imageUrl).into(ivPostImage)
//    Glide.with(context).load(model.coachProfilePicUri).into(ivPostImage)
   // tvPostAuthor.text=model.coachName
    tvPostText.text=model.text
   // tvPostAuthor.text=model.type
    tvType.text=model.type


    try {

//        val nameRef=db.collection("CoachDetails")
//            .document(model?.coachEmail).get().addOnSuccessListener {
//                val freeCount=it.get("coachName")
//                tvPostAuthor.text=freeCount.toString()
//
//            }

        val imageRef=db.collection("CoachDetails")
            .document(model?.coachEmail).get().addOnSuccessListener {
                val freeCount=it.get("coachProfilePicUri")
                Glide.with(context).load(freeCount).centerCrop().into(ivAuthorProfileImage)
            }
        val nameref=db.collection("CoachDetails").document(model?.coachEmail).get()
            .addOnSuccessListener {
                val freeCount=it.getString("coachName")
                tvPostAuthor.text=freeCount

        }

    }catch (e:Exception){
         Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
        Log.d("RECYCLER", "onBindViewHolder: ${e.message}")
    }



}


    }

}

