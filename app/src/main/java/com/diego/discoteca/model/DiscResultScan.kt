package com.diego.discoteca.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscResultScan(
    val barcode: String,
    val code: String
): Parcelable
