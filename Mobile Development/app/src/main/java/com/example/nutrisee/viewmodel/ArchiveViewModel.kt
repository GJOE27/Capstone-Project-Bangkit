package com.example.nutrisee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisee.data.remote.response.ListImageResponse
import com.example.nutrisee.data.repository.Repository
import com.example.nutrisee.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ArchiveViewModel(private val repository: Repository) : ViewModel() {

    private val listImage = MutableLiveData<Result<ListImageResponse>>()
    private var job: Job? = null

    fun getListImages() {
        job = viewModelScope.launch {
            repository.getListImage().collectLatest {
                listImage.value = it
            }
        }
    }

    val responseListImage: LiveData<Result<ListImageResponse>> = listImage

    fun getEmail(): String {
        return repository.getEmail()
    }

    fun logout() = viewModelScope.launch { repository.deleteUser() }


}