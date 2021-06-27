package com.example.bumpify.api

import com.example.bumpify.model.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

interface SimpleApi {

    @GET("surroundings")
    suspend fun getPost(@Header("location") location:String): Post

}