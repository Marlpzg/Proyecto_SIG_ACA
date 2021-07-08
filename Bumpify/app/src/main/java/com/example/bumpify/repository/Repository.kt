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
package com.example.bumpify.repository

import com.example.bumpify.api.RetrofitInstance
import com.example.bumpify.model.*
import retrofit2.Response

class Repository {

    //Acceder a todas las instancias de Retrofit

    suspend fun getPost(location:String): Post {
        return RetrofitInstance.api.getPost(location)
    }

    suspend fun pushUser(user: User): Response<Respuesta>{
        return RetrofitInstance.api.pushUser(user);
    }

    suspend fun getUs(usuario: String, password: String): Response<UserReq>{
        return RetrofitInstance.api.getUs(usuario, password)
    }

    suspend fun pushReport(report: ReportModel): Response<Respuesta>{
        return RetrofitInstance.api.pushReport(report)
    }



}