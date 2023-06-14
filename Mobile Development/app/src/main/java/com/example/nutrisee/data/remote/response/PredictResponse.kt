package com.example.nutrisee.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictResponse(
    @field:SerializedName("Makanan anda adalah")
    val message: String? = null,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("kalori")
    val kalori: Number
) : Parcelable