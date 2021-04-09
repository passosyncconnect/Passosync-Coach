package com.pasosync.pasosynccoach.repositories

import com.pasosync.pasosynccoach.db.ArticleDatabase
import com.pasosync.pasosynccoach.api.RetrofitInstance
import com.pasosync.pasosynccoach.db.LectureDatabase

class MainRepository(
  val db:LectureDatabase,
val newsdb: ArticleDatabase
) {

    suspend fun getCricketNews(query:String)=
        RetrofitInstance.api.getCricketNews(query)





}