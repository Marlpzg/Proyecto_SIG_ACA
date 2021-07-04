package com.example.bumpify.repository

import com.example.bumpify.api.RetrofitInstance
import com.example.bumpify.model.*
import retrofit2.Response

class Repository {

    suspend fun getPost(location:String): Post {
        return RetrofitInstance.api.getPost(location)
    }


    suspend fun pushUser(user: User): Response<User> {
        return RetrofitInstance.api.pushUser(user);
    }

    suspend fun getUser(userSignIn: UserSignIn): Response<UserSignIn> {
        return RetrofitInstance.api.getUser(userSignIn);
    }
    suspend fun getUs(usuario: String, password: String): Response<UserReq>{
        return RetrofitInstance.api.getUs(usuario, password)
    }
    suspend fun pushReport(report: ReportModel): Response<ReportModel>{
        return RetrofitInstance.api.pushReport(report)
    }

}