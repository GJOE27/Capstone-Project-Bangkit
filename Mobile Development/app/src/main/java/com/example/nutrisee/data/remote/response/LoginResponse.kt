package com.example.nutrisee.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @field:SerializedName("id_token")
    val id_token: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("refresh_token")
    val refresh_token: String? = null,

    @field:SerializedName("user")
    val loginResult: LoginResult? = null
) : Parcelable

@Parcelize
data class LoginResult(
    @field:SerializedName("uid")
    val uid: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("display_name")
    val display_name: String = "User",

    @field:SerializedName("photo_url")
    val photo_url: String? = "https://mm.widyatama.ac.id/wp-content/uploads/2020/08/dummy-profile-pic-male1.jpg",
) : Parcelable