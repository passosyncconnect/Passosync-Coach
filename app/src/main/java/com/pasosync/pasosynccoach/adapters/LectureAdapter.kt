package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.lecture_layout.view.*

class LectureAdapter(var mList:List<NewLectureDetails>) :
    RecyclerView.Adapter<LectureAdapter.LectureViewHolder>() {

    inner class LectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private var onItemClickListener: ((NewLectureDetails) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        return LectureViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.lecture_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LectureViewHolder,
        position: Int,

    ) {
        holder.itemView.apply {
            tv_lectureTitle.text = mList[position].lectureName
            tv_LectureDetails.text =mList[position].lectureContent
            lecture_date.text = mList[position].date
            type_lecture.text=mList[position].type
            setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position])
                }
            }

        }
    }
    fun deleteItem(position: Int) {
//        snapshots.getSnapshot(position).reference.delete()
    }
    fun setOnItemClickListener(listener: (NewLectureDetails) -> Unit) {
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return mList.size
    }

}