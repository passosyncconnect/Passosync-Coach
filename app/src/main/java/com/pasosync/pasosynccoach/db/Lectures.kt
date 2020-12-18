package com.pasosync.pasosynccoach.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "lectures")
 data class Lectures (

var lectureName:String?="",
var date:String?="",
 var lectureContent:String?=""
 ):Serializable
{
 @PrimaryKey(autoGenerate = true)
 var id:Int?=null
}