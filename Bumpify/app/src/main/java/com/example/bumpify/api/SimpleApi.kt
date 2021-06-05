package com.example.bumpify.api

import com.example.bumpify.model.Post
import retrofit2.http.GET

interface SimpleApi {

    @GET("test")
    suspend fun getPost(): Post

}