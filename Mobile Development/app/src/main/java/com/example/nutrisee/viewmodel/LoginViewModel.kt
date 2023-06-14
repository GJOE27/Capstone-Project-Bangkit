package com.example.nutrisee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisee.data.local.User
import com.example.nutrisee.data.repository.Repository
import com.example.nutrisee.utils.Result
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: Repository
) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password).asLiveData()

    fun saveUser(user: User) = viewModelScope.launch { repository.saveUser(user) }

}