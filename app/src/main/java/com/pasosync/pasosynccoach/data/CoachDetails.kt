package com.pasosync.pasosynccoach.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class CoachDetails(
    var coachName: String = "",
    var coachEmail: String = "",
    var coachMobile: String = "",
    var coachAbout: String = "",
    var coachProfilePicUri: String = "https://firebasestorage.googleapis.com/v0/b/pasosync-coach-2be77.appspot.com/o/Coach%2Fman.png?alt=media&token=8160454f-271d-4884-b3cd-8c1f93b27a1e",
    var coachIntroVideoUri: String = "",
    var coachExperience: String = "",
    var coachType: String = "",
    var follows: List<String> = listOf(),
    @get:Exclude
    var isFollowing: Boolean = false,
    var verify: Boolean =false
) : Serializable