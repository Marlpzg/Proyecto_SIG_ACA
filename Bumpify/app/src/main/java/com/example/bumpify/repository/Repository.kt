package com.example.bumpify.repository

import com.example.bumpify.api.RetrofitInstance
import com.example.bumpify.model.Post

class Repository {

    suspend fun getPost(): Post {
        return RetrofitInstance.api.getPost()
    }
}