package com.example.nutrisee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nutrisee.data.repository.Repository

class SignUpViewModel(
    private val repository: Repository
) : ViewModel() {
    fun signUpUser(email: String, password: String) =
        repository.signUp(email, password).asLiveData()

}