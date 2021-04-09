package com.pasosync.pasosynccoach.repositories

import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.Comment
import com.pasosync.pasosynccoach.data.Post
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.other.Resource

interface MainRepos {


    suspend fun getUser(uid: String): Resource<UserDetails>
    suspend fun getPostsForFollows(): Resource<List<Post>>
    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>
    suspend fun getUsers(uids: List<String>): Resource<List<UserDetails>>
    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>
    suspend fun getCommentCounterForPost(post: Post): Resource<String>
    suspend fun createComment(commentText: String, postId: String): Resource<Comment>
    suspend fun deletePost(post: Post): Resource<Post>
    suspend fun deleteComment(comment: Comment): Resource<Comment>

    suspend fun getCommentForPost(postId: String): Resource<List<Comment>>

    suspend fun getPostsForProfile(uid: String): Resource<List<Post>>


}


