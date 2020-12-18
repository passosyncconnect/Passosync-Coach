package com.pasosync.pasosynccoach.data

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class NewLectureDetails(
    var documentId: String? = "",
    var lectureName: String? = "",
    var lectureContent: String? = "",
    var lectureImageUrl: String? = "",
    var lectureVideoUrl: String? = "",
    var lecturePdfUrl: String? = "",
    var date: String? = null,
    var seacrh: String? = "",
    var type: String? = "",
    var timestamp: Long=0L
) : Serializable