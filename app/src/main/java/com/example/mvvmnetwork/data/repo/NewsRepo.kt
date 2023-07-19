package com.example.mvvmnetwork.data.repo

import com.mvvmnews.api.NewsClient

object NewsRepo{
    val api = NewsClient().newApi

    suspend fun getBreakingNews() = api.getBreakingNews()
}