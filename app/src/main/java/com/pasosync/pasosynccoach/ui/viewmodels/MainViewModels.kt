package com.pasosync.pasosynccoach.ui.viewmodels


import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.pasosync.pasosynccoach.Repositories.MainRepository
import com.pasosync.pasosynccoach.db.LectureResponse
import com.pasosync.pasosynccoach.db.Lectures
import com.pasosync.pasosynccoach.models.NewsResponse
import com.pasosync.pasosynccoach.other.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class MainViewModels(

    private val mainRepository: MainRepository
) : ViewModel() {



    val cricketNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()





    init {
        getcricketNews("cricket")

    }

    fun getcricketNews(query:String)=viewModelScope.launch {
        cricketNews.postValue(Resource.Loading())
        val response=mainRepository.getCricketNews(query)
        cricketNews.postValue(handleCricketNewsResponse(response))
    }
    private fun handleCricketNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }




//    fun insertRun(lectures: Lectures) = viewModelScope.launch {
//        mainRepository.insertLecture(lectures)
//    }


}