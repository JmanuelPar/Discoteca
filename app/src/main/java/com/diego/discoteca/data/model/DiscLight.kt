package com.diego.discoteca.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscLight(
    val id: Long,
    val name: String,
    val title: String,
    val year: String,
    val country: String,
    var format: String,
    var formatMedia: String
) : Parcelable