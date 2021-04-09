package com.pasosync.pasosynccoach.ui.viewmodels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.pasosync.pasosynccoach.BaseApplication
import com.pasosync.pasosynccoach.data.Comment
import com.pasosync.pasosynccoach.repositories.MainRepos
import com.pasosync.pasosynccoach.repositories.MainRepository
import com.pasosync.pasosynccoach.data.Post
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.models.NewsResponse
import com.pasosync.pasosynccoach.other.Resource
import com.pasosync.pasosynccoach.other.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class MainViewModels(
val app:Application,
    private val mainRepository: MainRepository,
    private val repos: MainRepos
) : AndroidViewModel(app) {
val user=FirebaseAuth.getInstance().currentUser?.uid!!

    private val _createCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val createCommentStatus: LiveData<Event<Resource<Comment>>> = _createCommentStatus

    private val _deleteCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val deleteCommentStatus: LiveData<Event<Resource<Comment>>> = _deleteCommentStatus

    private val _commentsForPost = MutableLiveData<Event<Resource<List<Comment>>>>()
    val commentsForPost: LiveData<Event<Resource<List<Comment>>>> = _commentsForPost

    private val _counterForPost = MutableLiveData<Event<Resource<String>>>()
    val counterForPost: LiveData<Event<Resource<String>>> = _counterForPost

    private val _deletePostStatus = MutableLiveData<Event<Resource<Post>>>()
    val deletePostStatus: LiveData<Event<Resource<Post>>> = _deletePostStatus


    private val _likePostStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val likePostStatus: LiveData<Event<Resource<Boolean>>> = _likePostStatus

    private val _likedByUsers = MutableLiveData<Event<Resource<List<UserDetails>>>>()
    val likedByUsers: LiveData<Event<Resource<List<UserDetails>>>> = _likedByUsers

    private val _followStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followStatus: LiveData<Event<Resource<Boolean>>> = _followStatus

    val cricketNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()
    val posts: LiveData<Event<Resource<List<Post>>>>
        get() = _posts

    private val _postsProfile = MutableLiveData<Event<Resource<List<Post>>>>()
    val postsProfile: LiveData<Event<Resource<List<Post>>>>
        get() = _postsProfile

    init {
        getcricketNews("cricket")
        getPosts("")
       // getPostsProfile(user)
        getPostsProfile("")


    }

    fun getcricketNews(query: String) = viewModelScope.launch {
        safeBreakingNewsCall(query)
//        cricketNews.postValue(Resource.Loading())
//        val response = mainRepository.getCricketNews(query)
//        cricketNews.postValue(handleCricketNewsResponse(response))
    }

    private fun handleCricketNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


//    fun insertRun(lectures: Lectures) = viewModelScope.launch {
//        mainRepository.insertLecture(lectures)
//    }


    fun getPosts(uid: String) {
        _posts.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getPostsForFollows()
            _posts.postValue(Event(result))
        }
    }


     fun getPostsProfile(uid: String) {
        _postsProfile.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getPostsForProfile(uid)
            _postsProfile.postValue(Event(result))
        }
         getPosts(uid)
    }




    fun toggleFollowForUser(uid: String) {
        _followStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun toggleLikeForPost(post: Post) {
        _likePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.toggleLikeForPost(post)
            _likePostStatus.postValue(Event(result))
        }
    }



    fun getUsers(uids: List<String>) {
        if(uids.isEmpty()) return
        _likedByUsers.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getUsers(uids)
            _likedByUsers.postValue(Event(result))
        }
    }

    fun createComment(commentText: String, postId: String) {
        if(commentText.isEmpty()) return
        _createCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createComment(commentText, postId)
            _createCommentStatus.postValue(Event(result))
        }
    }

    fun getCommentCounter(post: Post){

        _counterForPost.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result=repos.getCommentCounterForPost(post)
            _counterForPost.postValue(Event(result))

        }

    }

    fun deleteComment(comment: Comment) {
        _deleteCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.deleteComment(comment)
            _deleteCommentStatus.postValue(Event(result))
        }
    }

    fun getCommentsForPost(postId: String) {
        _commentsForPost.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getCommentForPost(postId)
            _commentsForPost.postValue(Event(result))
        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.deletePost(post)
            _deletePostStatus.postValue(Event(result))
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        cricketNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = mainRepository.getCricketNews(countryCode)
                cricketNews.postValue(handleCricketNewsResponse(response))
            } else {
                cricketNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {

            when (t) {
                is IOException -> cricketNews.postValue(Resource.Error("Network Failure"))
                else -> cricketNews.postValue(Resource.Error("Conversion Failure"))
            }
        }
    }



    private fun hasInternetConnection(): Boolean { // you can check anywhere by this method
        val connectivityManager = getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetworkState = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetworkState) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }


        }

        connectivityManager.activeNetworkInfo?.run {
            return when (type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false

            }
        }
        return false

    }

}