package com.pasosync.pasosynccoach.Repositories

import com.bairwa.newsapp.database.ArticleDatabase
import com.pasosync.pasosynccoach.api.RetrofitInstance
import com.pasosync.pasosynccoach.db.LectureDao
import com.pasosync.pasosynccoach.db.LectureDatabase
import com.pasosync.pasosynccoach.db.Lectures
import com.pasosync.pasosynccoach.models.Article

class MainRepository(
  val db:LectureDatabase,
val newsdb:ArticleDatabase
) {

    suspend fun getCricketNews(query:String)=
        RetrofitInstance.api.getCricketNews(query)



}