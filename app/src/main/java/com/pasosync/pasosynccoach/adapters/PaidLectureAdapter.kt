package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.ui.dialogs.DeletePostDialog
import kotlinx.android.synthetic.main.lecture_layout.view.*

class PaidLectureAdapter(options: FirestoreRecyclerOptions<NewLectureDetails>)
    :FirestoreRecyclerAdapter<NewLectureDetails,PaidLectureAdapter.FreeLectureViewHolder>(options)
{
    inner class FreeLectureViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((NewLectureDetails) -> Unit)? = null
    private var onDeleteClickListener: ((NewLectureDetails) -> Unit)? = null

    fun setOnItemClickListener(listener: (NewLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (NewLectureDetails) -> Unit) {
        onDeleteClickListener = listener
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
//            tv_lectureTitle.text = model?.lectureName
//            tv_LectureDetails.text =model?.lectureContent
//            lecture_date.text = model?.date
//            type_lecture.text=model?.type
//            category_lecture.text=model?.seacrh

            Glide.with(context).load(model.lectureImageUrl).placeholder(R.drawable.progressbg).into(iv_lecture_list_item)
tv_title_lecture_list_item.text=model.lectureName
            tv_date_lecture_list_item.text=model.date
            tv_lecture_list_item_type.text=model.seacrh
            tv_description_lecture_list_item.text=model.lectureContent

            ivDeleteLecture.setOnClickListener {
               onDeleteClickListener?.let {
                   it(model)
               }

            }

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