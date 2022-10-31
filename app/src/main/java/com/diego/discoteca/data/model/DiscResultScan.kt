package com.diego.discoteca.data.model

import android.os.Parcelable
import com.diego.discoteca.util.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscResultScan(
    val barcode: String,
    val destination: Destination
) : Parcelable
