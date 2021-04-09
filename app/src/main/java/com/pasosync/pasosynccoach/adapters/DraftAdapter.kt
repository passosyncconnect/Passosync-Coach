package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.DraftLectureDetails
import kotlinx.android.synthetic.main.draft_item_layout.view.*

class DraftAdapter(var mList: List<DraftLectureDetails>) :
    RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((DraftLectureDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (DraftLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        return DraftViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.draft_item_layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        holder.itemView.apply {
            tv_draft_title_item.text=mList[position].lectureTitleCoach
            tv_draft_description_item.text=mList[position].lectureContentCoach
            bt_draft_edit_item.setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position])
                }
            }
            bt_draft_delete_item.setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position])
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

}