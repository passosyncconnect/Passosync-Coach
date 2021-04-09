package com.pasosync.pasosynccoach.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.Post
import kotlinx.android.synthetic.main.item_post.view.*

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsLike = firestore.collection("Posts")
    private val auth = FirebaseAuth.getInstance()
    private val users = firestore.collection("UserDetails")
    var isFollowing = false

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage: ImageView = itemView.ivPostImage
        val ivAuthorProfileImage: ImageView = itemView.ivAuthorProfileImage
        val tvPostAuthor: TextView = itemView.tvPostAuthor
        val tvType: TextView = itemView.tvType
        val tvPostText: TextView = itemView.tvPostText
        val tvLikedBy: TextView = itemView.tvLikedBy
        val ibLike: ImageButton = itemView.ibLike
        val ibComments: ImageButton = itemView.ibComments
        val ibDeletePost: ImageButton = itemView.ibDeletePost
        val tvDatePost: TextView = itemView.tvDatePost
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]


        holder.apply {

            if (post.imageUrl.isEmpty()) {
                ivPostImage.visibility = View.GONE
            } else {
                Glide.with(itemView.context).load(post.imageUrl).into(ivPostImage)
            }

//
//
//            Glide.with(itemView.context).load(post.userPicUri).placeholder(R.drawable.man)
//                .into(ivAuthorProfileImage)


//            glide.load(post.imageUrl).into(ivPostImage)
//            glide.load(post.authorProfilePictureUrl).into(ivAuthorProfileImage)
            try {
                users.document(post.authorUid).get().addOnSuccessListener {
                    val name = it.getString("userName")!!
                    val userpic = it.getString("userPicUri")

                    tvPostAuthor.text = name
                    Glide.with(itemView.context).load(userpic).placeholder(R.drawable.img_avatar)
                        .into(ivAuthorProfileImage)


                }

            } catch (e: Exception) {
                Log.e("Post Adapter", "onBindViewHolder: ${e.message}")
            }

            //  tvPostAuthor.text = post.userName
            tvPostText.text = post.text

//            val calendar = post.date
//            val currentDate: String =
//                DateFormat.getDateInstance().format()

            val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
            val date = getLongToAgo(post.date)
            tvDatePost.text = date.toString()


            val commentCount = post.commentBy.size.toString()
            Log.d("COMMENT", "onBindViewHolder: ${commentCount}")

            val likeCount = post.likedBy.size
//            tvLikedBy.text = when {
//                likeCount <= 0 -> "No likes"
//                likeCount == 1 -> "Liked by 1 person"
//                else -> "Liked by $likeCount people"
//            }
            tvLikedBy.text = when {
                likeCount <= 0 -> ""
                else -> "${likeCount.toString()} likes"
            }
            tvType.text = post.type
            val uid = FirebaseAuth.getInstance().uid!!
            ibDeletePost.isVisible = uid == post.authorUid
//            ibLike.setImageResource(
//                if (post.isLiked) {
//                    R.drawable.ic_like
//                } else {
//                    R.drawable.ic_like_black
//                }
//            )


            ibLike.setImageResource(
                if (post.isLiked) {
                    R.drawable.ic_like
                } else R.drawable.ic_like_border
            )


            var email = auth.currentUser?.uid!!
            firestore.runTransaction { transaction ->
                val currentUser =
                    transaction.get(postsLike.document(post.id))
                        .toObject(Post::class.java)!!
                isFollowing = email in currentUser.likedBy
                if (isFollowing) {
                    //   Log.d(TAG, "onBindViewHolder:$isFollowing ")
                    ibLike.setImageResource(R.drawable.ic_like)
                } else {
                    //   Log.d(TAG, "else:$isFollowing ")
                    ibLike.setImageResource(R.drawable.ic_like_border)
                }

            }


//            if (post.isLiked) {
//                Log.d(TAG, "onBindViewHolder: ${post.isLiked}")
//                ibLike.setImageResource(R.drawable.ic_like)
//            } else {
//                Log.d(TAG, "else:${post.isLiked} ")
//                ibLike.setImageResource(R.drawable.ic_like_black)
//            }

            tvPostAuthor.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            ivAuthorProfileImage.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            tvLikedBy.setOnClickListener {
                onLikedByClickListener?.let { click ->
                    click(post)
                }
            }
            ibLike.setOnClickListener {
                onLikeClickListener?.let { click ->
                    if (!post.isLiking) click(post, holder.layoutPosition)
                }
            }
            ibComments.setOnClickListener {
                onCommentsClickListener?.let { click ->
                    click(post)
                }
            }
            ibDeletePost.setOnClickListener {
                onDeletePostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onDeletePostClickListener: ((Post) -> Unit)? = null
    private var onLikedByClickListener: ((Post) -> Unit)? = null
    private var onCommentsClickListener: ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeletePostClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }

    fun setOnLikedByClickListener(listener: (Post) -> Unit) {
        onLikedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener: (Post) -> Unit) {
        onCommentsClickListener = listener
    }

    private fun getLongToAgo(createdAt: Long): String? {
        val userDateFormat: DateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
        val dateFormatNeeded: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:MM:SS")
        var date: Date? = null
        date = Date(createdAt)
        var crdate1 = dateFormatNeeded.format(date)

        // Date Calculation
        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        crdate1 = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date)

        // get current date time with Calendar()
        val cal = Calendar.getInstance()
        val currenttime = dateFormat.format(cal.time)
        var CreatedAt: Date? = null
        var current: Date? = null
        try {
            CreatedAt = dateFormat.parse(crdate1)
            current = dateFormat.parse(currenttime)
        } catch (e: ParseException) {

            e.printStackTrace()
        }

        // Get msec from each, and subtract.
        val diff = current!!.time - CreatedAt!!.time
        val diffSeconds = diff / 1000
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        var time: String? = null
        if (diffDays > 0) {
            time = if (diffDays == 1L) {
                "$diffDays day ago "
            } else {
                "$diffDays days ago "
            }
        } else {
            if (diffHours > 0) {
                time = if (diffHours == 1L) {
                    "$diffHours hr ago"
                } else {
                    "$diffHours hrs ago"
                }
            } else {
                if (diffMinutes > 0) {
                    time = if (diffMinutes == 1L) {
                        "$diffMinutes min ago"
                    } else {
                        "$diffMinutes mins ago"
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = "$diffSeconds secs ago"
                    }
                }
            }
        }
        return time
    }


}