package com.example.Globalens.API_Services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("top-headlines")
    suspend fun getNews(
        @Query("country") country: String = "in",
        @Query("lang") language: String = "en",
        @Query("category") category : String = "",
        @Query("q") query : String = "",
        @Query("apikey") apikey: String
    ): Response<NewsApi_Class>

}