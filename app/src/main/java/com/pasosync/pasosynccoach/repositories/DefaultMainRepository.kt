package com.pasosync.pasosynccoach.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.Comment
import com.pasosync.pasosynccoach.data.Post
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.other.Resource
import com.passosync.socialnetwork.repositories.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

class DefaultMainRepository : MainRepos {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = firestore.collection("UserDetails")
    private val posts = firestore.collection("Posts")
    private val comments = firestore.collection("comments")


//
//    override suspend fun getUser(uid: String)= withContext(Dispatchers.IO) {
//safeCall {
//    val user = users.document(uid).get().await().toObject(UserDetails::class.java)
//        ?: throw IllegalStateException()
//    val currentUid = FirebaseAuth.getInstance().currentUser!!.email.toString()
//    val currentUser =
//        users.document(currentUid).get().await().toObject(UserDetails::class.java)
//            ?: throw IllegalStateException()
//    user.isFollowing = uid in currentUser.follows
//    Resource.Success(user)
//
//}
//    }

    override suspend fun getPostsForFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid!!
            val follows = getUser(uid).data!!.followsCoaches
            val allPosts = posts.whereIn("authorUid", follows)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    firestore.collection("UserDetails").document(post.authorUid).get()
                        .addOnSuccessListener {
                            val pic = it.getString("userPicUri")
                            val name = it.getString("userName")
                            post.userName = name!!
                            post.userPicUri = pic!!

                        }

                }

//                .onEach {
//                    val user = getUser(it.coachEmail).data!!
//
//
//
//
//                   // post.authorProfilePictureUrl = user.profilePictureUrl
//                  //  post.authorUsername = user.username
//                  //  post.isLiked = uid in post.likedBy
//                }
            Resource.Success(allPosts)

        }

    }

    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(UserDetails::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid!!
            val currentUser =
                users.document(currentUid).get().await().toObject(UserDetails::class.java)
                    ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.followsCoaches
            Resource.Success(user)
        }
    }

    override suspend fun toggleFollowForUser(uid: String): Resource<Boolean> =
        withContext(Dispatchers.IO)
        {
            safeCall {
                var isFollowing = false
                firestore.runTransaction { transaction ->
                    val currentUid = auth.currentUser?.uid!!
                    val currentUser =
                        transaction.get(users.document(currentUid))
                            .toObject(UserDetails::class.java)!!
                    isFollowing = uid in currentUser.followsCoaches
                    val newFollows =
                        if (isFollowing) currentUser.followsCoaches - uid else currentUser.followsCoaches + uid
                    transaction.update(users.document(currentUid), "followsCoaches", newFollows)
                }.await()
                Resource.Success(!isFollowing)
            }
        }

    override suspend fun getUsers(uids: List<String>) = withContext(Dispatchers.IO) {
        safeCall {
            val usersList = users.whereIn("uid", uids).orderBy("userName").get().await()
                .toObjects(UserDetails::class.java)
            Resource.Success(usersList)
        }
    }

    override suspend fun toggleLikeForPost(post: Post)= withContext(Dispatchers.IO) {
        safeCall {
            var isLiked = false
            firestore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid!!
               // val emailId=post.coachEmail
                val postResult = transaction.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    posts.document(post.id),
                    "likedBy",
                    if (uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    override suspend fun getCommentCounterForPost(post: Post)= withContext(Dispatchers.IO){
        safeCall {
            var count=""
            firestore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid!!
                val postResult = transaction.get(posts.document(post.id))
                val counter=postResult.toObject(Post::class.java)?.commentBy ?: listOf()

                transaction.update(
                    posts.document(post.id),
                    "commentBy",
                    counter + uid
                )
                count= counter.size.toString()

            }.await()

            Resource.Success(count)
        }

    }



    override suspend fun createComment(commentText: String, postId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                var countList:List<String> = mutableListOf()
                val uid = auth.currentUser?.uid!!
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId,
                    postId,
                    uid,
                    user.userName,
                    user.userPicUri,
                    commentText,uid
                )
                comments.document(commentId).set(comment).await()

                Resource.Success(comment)
            }
        }


    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            Resource.Success(comment)
        }
    }

    override suspend fun getCommentForPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentsForPost = comments
                .whereEqualTo("postId", postId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.username = user.userName
                    comment.profilePictureUrl = user.userPicUri
                }
            Resource.Success(commentsForPost)
        }
    }

    override suspend fun getPostsForProfile(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val profilePosts = posts.whereEqualTo("authorUid", uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorUid).data!!
                    post.userPicUri = user.userPicUri
                    post.userName = user.userName
                    post.isLiked = uid in post.likedBy
                }
            Resource.Success(profilePosts)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            if (post.imageUrl.isNotEmpty()){
                storage.getReferenceFromUrl(post.imageUrl).delete().await()
            }

            Resource.Success(post)
        }
    }




}