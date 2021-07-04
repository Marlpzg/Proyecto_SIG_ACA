package com.example.bumpify

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.*
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel( private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Post> = MutableLiveData()
    val myUser: MutableLiveData<Response<User>> = MutableLiveData()
    val myReport: MutableLiveData<Response<ReportModel>> = MutableLiveData()
    val getUs: MutableLiveData<Response<UserSignIn>> = MutableLiveData()
    val getUsu: MutableLiveData<Response<UserReq>> = MutableLiveData()

    fun getPost(location:String){
        viewModelScope.launch {
            val response = repository.getPost(location)
            myResponse.value = response
        }
    }

    fun pushUser(user: User){
        viewModelScope.launch {
            val response = repository.pushUser(user)
            myUser.value = response
        }
    }

    fun getUser(userSignIn: UserSignIn){
        viewModelScope.launch {
            val response = repository.getUser(userSignIn)


            getUs.value = response
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
            myReport.value = response
        }
    }

}