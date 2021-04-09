package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.GameScoreDetails

import kotlinx.android.synthetic.main.score_layout.view.*

class GameScoreAdapter(options: FirestoreRecyclerOptions<GameScoreDetails>):FirestoreRecyclerAdapter<GameScoreDetails,GameScoreAdapter.GameScoreViewHolder>(options) {


    inner class GameScoreViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((GameScoreDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (GameScoreDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameScoreViewHolder {
        return GameScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.score_layout,parent,false
            )
        )
    }

    override fun onBindViewHolder(
        holder: GameScoreViewHolder,
        position: Int,
        model: GameScoreDetails
    ) {

        holder.itemView.apply {
            itemMyTeamName.text=model?.myTeamName
            itemOpponentTeamName.text=model?.rivalTeamName
            itemMyTeamTotalScore.text="${model?.myTeamScore} (${model?.totalOver} Overs)"
            itemOpponentTeamTotalScore.text="${model?.rivalScore} (${model?.totalOver} Overs)"
            itemMyIndividualScore.text="${model?.myRuns} (${model?.ballPlayed} Balls)"
            itemDate.text=model?.date


            setOnClickListener {
                onItemClickListener?.let {
                    it(model)
                }
            }

        }



    }

}