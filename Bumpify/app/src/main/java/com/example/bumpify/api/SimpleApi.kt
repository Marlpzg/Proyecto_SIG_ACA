/*
Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 */
package com.example.bumpify.api

import com.example.bumpify.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SimpleApi {


    //Rutas a las que se realizan las peticiones de la app.


    @GET("surroundings")
    suspend fun getPost(@Header("location") location:String): Post

    @POST("users/add")
    suspend fun pushUser(
        @Body user: User
    ): Response<Respuesta>

    @GET("users/validate")
    suspend fun getUs(@Header("usuario") usuario: String, @Header("password") password: String): Response<UserReq>

    @POST("/newEvent")
    suspend fun pushReport(
        @Body report: ReportModel
    ): Response<Respuesta>
}