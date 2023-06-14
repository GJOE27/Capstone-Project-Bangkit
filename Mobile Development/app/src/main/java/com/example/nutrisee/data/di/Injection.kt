package com.example.nutrisee.data.di

import android.content.Context
import com.example.nutrisee.data.remote.api.ApiConfig
import com.example.nutrisee.data.repository.Repository

object Injection {
    fun provideRepository(context: Context) : Repository {
        val apiService = ApiConfig.getApiService()
        return Repository(apiService, context)
    }
}