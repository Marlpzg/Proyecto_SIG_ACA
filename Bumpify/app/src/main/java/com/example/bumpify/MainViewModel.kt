package com.example.bumpify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel( private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Post> = MutableLiveData()
    val myUser: MutableLiveData<Response<User>> = MutableLiveData()

    fun getPost(){
        viewModelScope.launch {
            val response = repository.getPost()
            myResponse.value = response
        }
    }

    fun pushUser(user: User){
        viewModelScope.launch {
            val response = repository.pushUser(user)
            myUser.value = response
        }
    }

}