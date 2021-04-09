package com.pasosync.pasosynccoach.data


import java.io.Serializable

data class PracticeVideoProgressData(
    var VideoUrlProgress: String? = "",
    var type: String? = "",
    var id: String? = "",
    var tag:String?="PracticeVideoFragment"


) : Serializable
