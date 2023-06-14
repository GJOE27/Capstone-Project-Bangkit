package com.example.nutrisee.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest (
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)