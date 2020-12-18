package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.NewLectureDetails
import kotlinx.android.synthetic.main.lecture_layout.view.*

class DashBoardAdapter(options: FirestoreRecyclerOptions<NewLectureDetails>) :
    FirestoreRecyclerAdapter<NewLectureDetails, DashBoardAdapter.DashBoardViewHolder>(options) {

    inner class DashBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var onItemClickListener: ((NewLectureDetails) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        return DashBoardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lecture_layout, parent, false)
        )
    }
    fun setOnItemClickListener(listener: (NewLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(
        holder: DashBoardViewHolder,
        position: Int,
        model: NewLectureDetails
    ) {
       holder.itemView.apply {
           tv_lectureTitle.text = model?.lectureName
           tv_LectureDetails.text = model?.lectureContent
           lecture_date.text = model?.date
           type_lecture.text=model?.type
           category_lecture.text=model?.seacrh
           setOnClickListener {
               onItemClickListener?.let {
                   it(model)
               }
           }

       }
    }
}