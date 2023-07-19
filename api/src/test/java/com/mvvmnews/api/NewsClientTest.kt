package com.mvvmnews.api

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertNotNull

//Junit lib
class NewsClientTest {
    private val newsClient = NewsClient()

    @Test
    fun `GET top-headlines`(){
        runBlocking {
            val news = newsClient.newApi.getBreakingNews()
            assertNotNull(news.body()?.articles)
        }
    }

    @Test
    fun `GET search-news`(){
        runBlocking {
            val news = newsClient.newApi.searchForNews("rahul").execute()
            assertNotNull(news.body()?.articles)
        }
    }
}