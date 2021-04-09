package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.android.synthetic.main.item_user_subscribed.view.*

class ConnectUserAdapter: RecyclerView.Adapter<ConnectUserAdapter.ConnectUserViewHolder>() {


    class ConnectUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePicture: ImageView = itemView.ivProfileImageUser
        val tvUsername: TextView = itemView.tvUsernameUser
    }

    private val diffCallback = object : DiffUtil.ItemCallback<UserDetails>() {
        override fun areContentsTheSame(oldItem: UserDetails, newItem: UserDetails): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: UserDetails, newItem: UserDetails): Boolean {
            return oldItem.userEmail == newItem.userEmail
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var users: List<UserDetails>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectUserViewHolder {
        return ConnectUserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_subscribed,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ConnectUserViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
         //   glide.load(user.profilePictureUrl).into(ivProfilePicture)
            Glide.with(holder.itemView.context).load(user.userPicUri).into(ivProfilePicture)

            tvUsername.text = user.userName
            itemView.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(user)
                }
            }
        }
    }

    private var onUserClickListener: ((UserDetails) -> Unit)? = null

    fun setOnUserClickListener(listener: (UserDetails) -> Unit) {
        onUserClickListener = listener
    }



}