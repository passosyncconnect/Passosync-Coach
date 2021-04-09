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

class PaidLectureAdapter(options: FirestoreRecyclerOptions<NewLectureDetails>)
    :FirestoreRecyclerAdapter<NewLectureDetails,PaidLectureAdapter.FreeLectureViewHolder>(options)
{
    inner class FreeLectureViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((NewLectureDetails) -> Unit)? = null

    fun setOnItemClickListener(listener: (NewLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeLectureViewHolder {
        return FreeLectureViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.lecture_layout,parent,false
            )

        )
    }

    override fun onBindViewHolder(
        holder: FreeLectureViewHolder,
        position: Int,
        model: NewLectureDetails
    ) {
        holder.itemView.apply {
            tv_lectureTitle.text = model?.lectureName
            tv_LectureDetails.text =model?.lectureContent
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

    fun deleteItem(position: Int) {
       snapshots.getSnapshot(position).reference.delete()
    }
}