package com.example.bumpify.api

import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SimpleApi {

    @GET("surroundings")
    suspend fun getPost(@Header("location") location:String): Post

    @POST("users/add")
    suspend fun pushUser(
        @Body user: User
    ): Response<User>
}