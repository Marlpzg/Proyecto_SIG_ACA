package com.example.bumpify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bumpify.model.Post
import com.example.bumpify.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel( private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Post> = MutableLiveData()

    fun getPost(location:String){
        viewModelScope.launch {
            val response = repository.getPost(location)
            myResponse.value = response
        }
    }

}