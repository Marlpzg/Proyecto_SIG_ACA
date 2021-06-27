package com.example.bumpify.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("passwd") val passwd: String,
    @SerializedName("gender") val gender: String
)