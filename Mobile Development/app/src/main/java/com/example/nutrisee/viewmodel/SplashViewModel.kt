package com.example.nutrisee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nutrisee.data.local.User
import com.example.nutrisee.data.repository.Repository

class SplashViewModel(private val repository: Repository) : ViewModel() {

    fun getUser(): LiveData<User> = repository.getUser()

}