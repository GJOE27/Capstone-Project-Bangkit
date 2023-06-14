package com.example.nutrisee.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpResponse(
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable