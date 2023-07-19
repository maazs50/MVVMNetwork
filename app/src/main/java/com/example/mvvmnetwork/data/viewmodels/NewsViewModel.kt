package com.example.mvvmnetwork.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmnetwork.data.repo.NewsRepo
import com.mvvmnews.api.models.Article
import kotlinx.coroutines.launch

class NewsViewModel: ViewModel() {
    private val _newsList = MutableLiveData<List<Article>>()

    val newsList: LiveData<List<Article>>
        get() = _newsList

    fun getNewsList() = viewModelScope.launch {
        NewsRepo.getBreakingNews().body()?.let {
            _newsList.postValue(it.articles)
            Log.d("Articles", "Articles fected ${it.totalResults}")
        }

    }
}