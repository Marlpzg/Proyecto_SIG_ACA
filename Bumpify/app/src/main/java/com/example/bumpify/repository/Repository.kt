package com.example.bumpify.repository

import com.example.bumpify.api.RetrofitInstance
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import retrofit2.Response

class Repository {

    suspend fun getPost(): Post {
        return RetrofitInstance.api.getPost()
    }


    suspend fun pushUser(user: User): Response<User> {
        return RetrofitInstance.api.pushUser(user);
    }
}