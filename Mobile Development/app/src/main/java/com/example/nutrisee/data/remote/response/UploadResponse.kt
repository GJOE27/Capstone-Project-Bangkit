package com.example.nutrisee.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @field:SerializedName("kalori")
    val kalori: Double? = null
)
