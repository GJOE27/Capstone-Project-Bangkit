package com.example.nutrisee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nutrisee.data.repository.Repository
import java.io.File

class UploadViewModel constructor(private val repository: Repository) : ViewModel() {

    private val mTempFile = MutableLiveData<File>()
    val tempFile: LiveData<File> = mTempFile

    fun setFile(file: File) {
        mTempFile.value = file
    }
}