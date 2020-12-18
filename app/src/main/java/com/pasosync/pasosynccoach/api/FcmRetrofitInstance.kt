package com.pasosync.pasosynccoach.api

import com.pasosync.pasosynccoach.other.Constant.FCM_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FcmRetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(FCM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val fcmApi by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }

}