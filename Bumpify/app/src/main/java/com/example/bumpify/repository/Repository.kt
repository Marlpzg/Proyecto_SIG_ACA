package com.example.bumpify.repository

import com.example.bumpify.api.RetrofitInstance
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import retrofit2.Response

class Repository {

    suspend fun getPost(location:String): Post {
        return RetrofitInstance.api.getPost(location)
    }


    suspend fun pushUser(user: User): Response<User> {
        return RetrofitInstance.api.pushUser(user);
    }
}