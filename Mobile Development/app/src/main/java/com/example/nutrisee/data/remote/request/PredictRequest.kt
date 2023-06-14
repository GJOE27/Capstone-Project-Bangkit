package com.example.nutrisee.data.remote.request

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import java.io.File

data class PredictRequest (
    @SerializedName("file")
    val file: RequestBody
)