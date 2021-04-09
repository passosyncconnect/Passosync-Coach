package com.pasosync.pasosynccoach.data

import java.io.Serializable

data class GameScoreDetails(
    var myTeamName:String?="",
    var rivalTeamName:String?="",
    var myTeamScore:Int=0,
    var rivalScore:Int=0,
    var myRuns:Int=0,
    var myFours:Int=0,
    var mySixes:Int=0,
    var myWicketTaken:Int=0,
    var ballPlayed:Int=0,
    var aboutMatchDescription:String?="",
    var date:String?="",
    var gameImageUrl:String?="",
    var gameVideoUrl:String?="",
    var timestamp:Long=0L,
    var totalOver:Int=0
):Serializable
