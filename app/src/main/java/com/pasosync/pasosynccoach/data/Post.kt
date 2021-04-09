package com.pasosync.pasosynccoach.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Post(
    val id: String = "",
    val authorUid: String = "",
    @get:Exclude var userName: String = "",
    @get:Exclude var userPicUri: String = "",

    val text: String = "",
    val imageUrl: String = "",
    val type:String="",
    val date: Long = 0L,
    @get:Exclude var isLiked: Boolean = false,
    @get:Exclude var isLiking: Boolean = false,
    var likedBy: List<String> = listOf(),
    var coachEmail:String="",
    var commentBy:List<String> = listOf()
):Serializable