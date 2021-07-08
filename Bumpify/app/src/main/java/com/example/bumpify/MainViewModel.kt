package com.example.bumpify

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.*
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

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