package com.example.mvvmnetwork.data.repo

import com.mvvmnews.api.NewsClient

object NewsRepo{
    val api = NewsClient().newApi

    suspend fun getBreakingNews(countryCode: String,pageNo: Int) = api.getBreakingNews(countryCode = countryCode, pageNumber = pageNo)

    suspend fun getSearchNews(query: String) = api.searchForNews(query)
}