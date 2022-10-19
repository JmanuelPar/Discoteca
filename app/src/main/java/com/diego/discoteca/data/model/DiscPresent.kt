package com.diego.discoteca.data.model

import android.os.Parcelable
import com.diego.discoteca.data.domain.Disc
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscPresent(
    var list: List<Disc>,
    val discAdd: DiscAdd
) : Parcelable