package com.pasosync.pasosynccoach.data

data class NotifyData(
    var coachName:String="",
    var coachEmail:String="",
    var lectureName:String?="",
    var lectureContent:String?="",
    var lectureImageUrl:String?="",
    var lectureVideoUrl:String?="",
    var lecturePdfUrl:String?="",
    var date:String?=null

)