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
package com.example.bumpify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.*
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Código Boilerplate necesario para el uso de la libreria Retrofit
 * Se puede ver más en: https://square.github.io/retrofit/
 */
class MainViewModel( private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Post> = MutableLiveData()
    val myRespuesta: MutableLiveData<Response<Respuesta>> = MutableLiveData()
    val getUsu: MutableLiveData<Response<UserReq>> = MutableLiveData()
    val getString: MutableLiveData<Response<String>> = MutableLiveData()

    fun getPost(location:String){
        viewModelScope.launch {
            val response = repository.getPost(location)
            myResponse.value = response
        }
    }

    fun pushUser(user: User){
        viewModelScope.launch {
            val response = repository.pushUser(user)
            myRespuesta.value = response
        }
    }


    fun getUsu(usuario: String, password: String){
        viewModelScope.launch{
            val response = repository.getUs(usuario, password)
            getUsu.value = response


        }
    }

    fun pushReport(report: ReportModel){
        viewModelScope.launch {
            val response = repository.pushReport(report)
            myRespuesta.value = response
        }
    }

}