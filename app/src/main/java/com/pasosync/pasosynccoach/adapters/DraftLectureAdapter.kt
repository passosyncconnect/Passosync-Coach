package com.pasosync.pasosynccoach.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.DraftLectureDetails
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.ui.dialogs.DeletePostDialog
import kotlinx.android.synthetic.main.draft_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "DraftLectureAdapter"
class DraftLectureAdapter(options: FirestoreRecyclerOptions<DraftLectureDetails>) :
    FirestoreRecyclerAdapter<DraftLectureDetails, DraftLectureAdapter.DraftLectureViewHolder>(
        options
    ) {

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var draftCollectionRef = db.collection("CoachLectureList")
        .document(user?.uid!!).collection("DraftLecture")

    inner class DraftLectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((DraftLectureDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (DraftLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftLectureViewHolder {
        return DraftLectureViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.draft_layout, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DraftLectureViewHolder,
        position: Int,
        model: DraftLectureDetails
    ) {
        holder.itemView.apply {
            tv_draft_title.text = model?.lectureTitleCoach
            tv_draft_description.text = model?.lectureContentCoach

            bt_draft_delete.setOnClickListener {
                Log.d(TAG, "onBindViewHolder: ")
               onItemClickListener?.let {
                   it(model)
                   Log.d(TAG, "onBindViewHolder:")
               }

            }


            bt_draft_edit.setOnClickListener {
                val bundle = Bundle().apply {
                    val title = tv_draft_title.text.toString()
                    val desc = tv_draft_description.text.toString()
                    val id=model.id
                    val draftLectureDetails = DraftLectureDetails(title, desc,id)
                    putSerializable("draft", draftLectureDetails)
                }

                findNavController().navigate(R.id.action_draftFragment_to_newPostFragment, bundle)
               // deleteItem(holder.adapterPosition)


            }

        }


    }

    fun deleteItem(position: Int) {
        snapshots.getSnapshot(position).reference.delete()
    }

    private fun retrieveLectureDetails() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var title_lecture: String? = null
            var des_lecture: String? = null
            val querySnapshot = draftCollectionRef.get().addOnSuccessListener {
                for (document in it.documents) {
                    val details = DraftLectureDetails(
                        document.getString(""), document.getString(""), document.id
                    )
                }


            }.await()

        } catch (e: Exception) {

        }
    }


}