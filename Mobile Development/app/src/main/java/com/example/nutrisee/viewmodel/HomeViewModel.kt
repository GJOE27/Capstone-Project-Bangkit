package com.example.nutrisee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisee.data.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel constructor(private val repository: Repository) : ViewModel() {

    fun getEmail(): String {
        return repository.getEmail()
    }

    fun logout() = viewModelScope.launch { repository.deleteUser() }

}