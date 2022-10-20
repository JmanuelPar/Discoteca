package com.diego.discoteca.data.model

import android.os.Parcelable
import com.diego.discoteca.util.AddBy
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscAdd(
    var id: Long = 0L,
    var name: String,
    var title: String,
    var year: String,
    var addBy: AddBy
) : Parcelable