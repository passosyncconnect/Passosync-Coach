package com.pasosync.pasosynccoach.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.pasosync.pasosynccoach.other.Constant
import java.io.Serializable

@IgnoreExtraProperties
data class UserDetails(

    var uid:String="",
    var userName:String="",
    var userEmail:String="",
    var userMobile:String="",
    var userAge:String="",
    var userAbout:String="",
    var userKind:String="",
    var userLevel:String="",
    var verify:Boolean=false,
    var experience:String="",
    var userType:String="coach",
    var academy:String="",
    var introVideoUri:String="",
    var userPicUri:String=Constant.PROFILE_PIC_PLACEHOLDER,
    var coachPlus:Boolean=false,
    var connectPlus:Boolean=false,
    var follows: List<String> = listOf(),
    var followsCoaches: List<String> = listOf(),
    var subscribed: List<String> = listOf(),

    @get:Exclude
    var isFollowing: Boolean = false
):Serializable