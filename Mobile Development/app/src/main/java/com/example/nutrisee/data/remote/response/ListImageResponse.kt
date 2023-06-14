package com.example.nutrisee.data.remote.response

import com.google.gson.annotations.SerializedName

data class ListImageResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("images")
    val listImageResult: List<ListImageResult>
)

data class ListImageResult(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("url")
    val url: String,

    @field:SerializedName("created_at")
    val createdAt: String
)

