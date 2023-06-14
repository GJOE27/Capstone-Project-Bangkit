package com.example.nutrisee.data.remote.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
//    @SerializedName("display_name") val display_name: String? = "User",
//    @SerializedName("photoUrl") val photo_url: String? = "https://mm.widyatama.ac.id/wp-content/uploads/2020/08/dummy-profile-pic-male1.jpg"
)
