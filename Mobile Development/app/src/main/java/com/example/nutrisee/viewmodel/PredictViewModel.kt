package com.example.nutrisee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nutrisee.data.repository.Repository
import java.io.File

class PredictViewModel(
    private val repository: Repository
) : ViewModel() {

    fun predict(file: File) = repository.predict(file).asLiveData()
}