package com.example.nutrisee.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id_token: String,
    val email: String
) : Parcelable