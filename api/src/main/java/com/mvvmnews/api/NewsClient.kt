package com.mvvmnews.api

import com.mvvmnews.api.services.NewsApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class NewsClient {
    val retrofit  = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val newApi = retrofit.create(NewsApi::class.java)

}