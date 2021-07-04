package com.example.bumpify.api

import com.example.bumpify.utils.Constants.Companion.REQ_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    var httpClient = OkHttpClient.Builder()
        .callTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(REQ_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

    }

    val api: SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }

}