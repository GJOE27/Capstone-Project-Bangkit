package com.example.nutrisee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nutrisee.data.repository.Repository
import java.io.File

class SignUpViewModel(
    private val repository: Repository
) : ViewModel() {
    fun signUpUser(email: String, password: String) =
        repository.signUp(email, password).asLiveData()

//    fun signUpUser(email: String, password: String, file: File? = null, displayName : String? = null) =
//        repository.signUp(email, password, file, displayName).asLiveData()

}