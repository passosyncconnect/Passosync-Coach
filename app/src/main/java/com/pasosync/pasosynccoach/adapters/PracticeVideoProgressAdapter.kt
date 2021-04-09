package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.PracticeVideoProgressData


class PracticeVideoProgressAdapter(options: FirestoreRecyclerOptions<PracticeVideoProgressData>)
    :FirestoreRecyclerAdapter<PracticeVideoProgressData,PracticeVideoProgressAdapter.VideoProgressViewHolder>(options) {

    inner class VideoProgressViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    private var onItemClickListener: ((PracticeVideoProgressData) -> Unit)? = null
    fun setOnItemClickListener(listener: (PracticeVideoProgressData) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoProgressViewHolder {
        return VideoProgressViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.video_progress_layout,parent,false
            )
        )
    }

    override fun onBindViewHolder(
        holder: VideoProgressViewHolder,
        position: Int,
        model: PracticeVideoProgressData
    ) {
        holder.itemView.apply {

            setOnClickListener {
                onItemClickListener?.let {
                    it(model)
                }
            }
        }


    }
}