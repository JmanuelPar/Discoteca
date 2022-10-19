package com.diego.discoteca.data.model

import android.os.Parcelable
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.util.AddBy
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscResultDetail(
    val disc: Disc,
    val addBy: AddBy
) : Parcelable