package com.example.mvvmnetwork.data.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmnetwork.NewsApplication
import com.example.mvvmnetwork.data.Resource
import com.example.mvvmnetwork.data.repo.NewsRepo
import com.mvvmnews.api.models.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

//We are using AndroidViewModel instead of viewModel bcoz we need Application context to be used for checking connection
class NewsViewModel(app: Application): AndroidViewModel(app) {
    private val _newsList= MutableLiveData<Resource<ApiResponse>>()
    val newsList: LiveData<Resource<ApiResponse>>
        get() = _newsList
    var pageNo = 1

    init {
        getBreakingNews("us",pageNo)
    }
    fun getBreakingNews(countryCode: String, pageNo: Int) = viewModelScope.launch {
        //Delegate to check connection and make request
        safeNetworkRequest(countryCode,pageNo)
    }

    private fun handleApiResponse(response: Response<ApiResponse>): Resource<ApiResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeNetworkRequest(countryCode: String,pageNo: Int){
        _newsList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = NewsRepo.getBreakingNews(countryCode,pageNo)
                _newsList.postValue(handleApiResponse(response))
            } else {
                _newsList.postValue(Resource.Error("No internet connection"))
            }

        } catch (t: Throwable){
            when(t){
               is IOException-> _newsList.postValue(Resource.Error("Network failure"))
                else-> _newsList.postValue(Resource.Error("Conversion error"))
            }
        }
    }
    //Check internet before any api request
    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApplication>().
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)-> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)-> true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else->false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false
                }
            }
        }
        return false
    }
}