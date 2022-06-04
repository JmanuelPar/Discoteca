package com.diego.discoteca.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscAdd(
    var id: Long = 0L,
    var name: String,
    var title: String,
    var year: String,
    var addBy: Int
) : Parcelable