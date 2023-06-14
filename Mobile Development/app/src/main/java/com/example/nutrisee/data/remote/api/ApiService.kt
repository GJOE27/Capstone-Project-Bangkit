package com.example.nutrisee.data.remote.api

import com.example.nutrisee.data.remote.request.LoginRequest
import com.example.nutrisee.data.remote.request.RegisterRequest
import com.example.nutrisee.data.remote.response.ListImageResponse
import com.example.nutrisee.data.remote.response.LoginResponse
import com.example.nutrisee.data.remote.response.SignUpResponse
import com.example.nutrisee.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): SignUpResponse

    @Multipart
    @POST("upload")
    suspend fun predict(
        @Header("Authorization") idToken: String,
        @Part photo: MultipartBody.Part
    ) : UploadResponse

    @GET("list_images")
    suspend fun getListImage(
        @Header("Authorization") token: String
    ): ListImageResponse
}