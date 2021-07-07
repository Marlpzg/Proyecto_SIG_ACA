package com.example.bumpify.api

import com.example.bumpify.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SimpleApi {

    @GET("surroundings")
    suspend fun getPost(@Header("location") location:String): Post

    @POST("users/add")
    suspend fun pushUser(
        @Body user: User
    ): Response<Respuesta>
    @POST("users/validate")
    suspend fun getUser(
        @Body userSignIn: UserSignIn
    ): Response<UserSignIn>

    @GET("users/validate")
    suspend fun getUs(@Header("usuario") usuario: String, @Header("password") password: String): Response<UserReq>

    @POST("/newEvent")
    suspend fun pushReport(
        @Body report: ReportModel
    ): Response<Respuesta>
}