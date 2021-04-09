package com.pasosync.pasosynccoach.api
import com.pasosync.pasosynccoach.models.NewsResponse
import com.pasosync.pasosynccoach.other.ApiKey.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
@GET("v2/top-headlines")
    suspend fun getCricketNews(
    @Query("q")
    Query: String,
    @Query("apiKey")
    apiKey: String = API_KEY
):Response<NewsResponse>

}