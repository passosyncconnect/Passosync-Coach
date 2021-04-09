package com.pasosync.pasosynccoach.data

import android.graphics.Bitmap
import java.io.Serializable

data class QueryDataRV(
    var timestamp:String?="",
    var query:String?="",
    var milliTimeStamp:Long?=0L,
    var img: Bitmap?=null,
    var id:String?="",
    var imgUrl:String?="",
    var parentId:String?="",
    var type:String?=""

):Serializable
