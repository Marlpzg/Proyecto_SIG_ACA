package com.example.bumpify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import com.example.bumpify.model.UserReq
import com.example.bumpify.model.UserSignIn
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel( private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Post> = MutableLiveData()
    val myUser: MutableLiveData<Response<User>> = MutableLiveData()
    val getUs: MutableLiveData<Response<UserSignIn>> = MutableLiveData()
    val getUsu: MutableLiveData<UserReq> = MutableLiveData()

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

    fun getUsu(usuario: String){
        viewModelScope.launch{
            val response = repository.getUs(usuario)
            getUsu.value = response
        }
    }

}