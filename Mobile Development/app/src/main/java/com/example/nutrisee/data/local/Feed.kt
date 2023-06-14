package com.example.nutrisee.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feed(
    val imageProfile: Int,
    val name: String,
    val caption: String,
    val imageFeed: Int,
    val date: String
) : Parcelable
