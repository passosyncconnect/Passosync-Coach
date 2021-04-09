package com.pasosync.pasosynccoach.data


import java.io.Serializable

data class VideoProgressData(
    var VideoUrlProgress: String? = "",
    var type: String? = "",
    var id: String? = "",
    var tag:String?="GameVideoFragment"

) : Serializable
