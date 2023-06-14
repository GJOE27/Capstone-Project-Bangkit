package com.example.nutrisee.data.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import com.example.nutrisee.data.local.Preferences
import com.example.nutrisee.data.local.User
import com.example.nutrisee.data.remote.api.ApiConfig
import com.example.nutrisee.data.remote.api.ApiService
import com.example.nutrisee.data.remote.request.LoginRequest
import com.example.nutrisee.data.remote.request.RegisterRequest
import com.example.nutrisee.data.remote.response.ListImageResponse
import com.example.nutrisee.utils.Helper.Companion.dataStore
import com.example.nutrisee.utils.Helper.Companion.reduceFileImage
import com.example.nutrisee.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class Repository(
    private val apiService: ApiService,
    context: Context
) {
    private val pref = Preferences.getInstance(context.dataStore)

    private fun getToken(): String {
        val result = runBlocking {
            withContext(Dispatchers.IO) {
                pref.getUser().first()
            }
        }
        return result.id_token
    }

    // Login 1
    fun login(email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.message == "User logged in successfully") {
                val idToken = response.id_token
                if(idToken != null) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error("Invalid token"))
                }
            } else emit(Result.Error(response.message as String))
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Result.Error(it) })
        }
    }

    fun signUp(email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val request = RegisterRequest(email, password)
            val response = apiService.register(request)
            if (response.message == "User registered successfully") {
                emit(Result.Success(response))
            }
            else emit(Result.Error(response.message as String))
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Result.Error(it) })
        }
    }

    fun predict(file: File) = flow {
        emit(Result.Loading())
        try {
            val token = getToken()
            val reducedFile = reduceFileImage(file)
            val requestFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val response = ApiConfig.getApiService().predict(token, multipartBody)
            if (response.message == "Image uploaded successfully") {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Failed to predict image"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage ?: "Failed to predict image"))
        }
    }

    fun upload(file: File, name: String, kalori: String) = flow {
        emit(Result.Loading())
        try {
            val token = getToken()
            val reducedFile = reduceFileImage(file)
            val requestFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val response = ApiConfig.getApiService().upload(token, multipartBody, name, kalori)
            if (response.message == "Image uploaded successfully") {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Failed to predict image"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage ?: "Failed to predict image"))
        }
    }

    suspend fun getListImage(): Flow<Result<ListImageResponse>> = flow {
        emit(Result.Loading())
        try {
            val token = getToken()
            val response = ApiConfig.getApiService().getListImage(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            val ex = (e as? HttpException)?.response()?.errorBody()?.string()
            emit(Result.Error(ex.toString()))
        }
    }.flowOn(Dispatchers.IO)

    // ini nanti (sudah termasuk pada predict)
//    fun upload(file: File) = flow {
//        emit(Result.Loading())
//        try {
//            val token = getToken()
//            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//            val multipartBody: MultipartBody.Part =
//                MultipartBody.Part.createFormData("image_file", file.name, requestFile)
//            val response = ApiConfig.getApiService().upload(token, multipartBody)
//            if(!response.error) { //
//                emit(Result.Success(response))
//            } else {
//                emit(Result.Error(response.message))
//            }
//        } catch (e: Exception) {
//            (e as? HttpException)?.response()?.errorBody()?.string()
//            emit(e.localizedMessage?.let { Result.Error(it) })
//        }
//    }

    fun getUser() = pref.getUser().asLiveData()

    suspend fun deleteUser() = pref.deleteUser()

    suspend fun saveUser(user: User) = pref.saveUser(user)
}