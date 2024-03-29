package com.diego.discoteca.util

import android.content.Context
import com.diego.discoteca.R
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.NetworkDiscDiscogs
import com.diego.discoteca.data.model.Result
import com.diego.discoteca.database.DatabaseDisc

fun List<DatabaseDisc>.asDomainModel(): List<Disc> {
    return map {
        Disc(
            id = it.id,
            name = it.name,
            title = it.title,
            year = it.year,
            country = it.country,
            format = it.format,
            formatMedia = it.formatMedia,
            coverImage = it.coverImage,
            barcode = it.barcode,
            idDisc = it.idDisc,
            addBy = it.addBy
        )
    }
}

fun DatabaseDisc.asDomainModel(): Disc {
    return Disc(
        id = this.id,
        name = this.name,
        title = this.title,
        year = this.year,
        country = this.country,
        format = this.format,
        formatMedia = this.formatMedia,
        coverImage = this.coverImage,
        barcode = this.barcode,
        idDisc = this.idDisc,
        addBy = this.addBy
    )
}

fun Disc.asDatabaseModel(): DatabaseDisc {
    return DatabaseDisc(
        id = this.id,
        name = this.name,
        title = this.title,
        year = this.year,
        country = this.country,
        format = this.format,
        formatMedia = this.formatMedia,
        coverImage = this.coverImage,
        barcode = this.barcode,
        idDisc = this.idDisc,
        addBy = this.addBy,
        nameNormalize = this.name.stringNormalizeDatabase(),
        titleNormalize = this.title.stringNormalizeDatabase()
    )
}

fun NetworkDiscDiscogs.asDomainModel(context: Context, barcode: String): List<Disc> {
    return this.results?.map { result ->
        processingResult(
            result = result,
            barcode = barcode,
            context = context
        )
    } ?: emptyList()
}

private fun processingResult(result: Result, barcode: String, context: Context): Disc {
    val formats = result.formats
    val formatQuantity = result.formatQuantity ?: -1
    val listFormatsSize = formats?.size?.minus(1)
    var myFormat = ""
    val listFormat = mutableListOf<String>()
    val listBarcodeDiscogs = result.barcode
    val barcodeDiscogs = barcode.ifEmpty {
        when {
            listBarcodeDiscogs != null && listBarcodeDiscogs.isNotEmpty() ->
                listBarcodeDiscogs[0]

            else -> ""
        }
    }

    formats?.forEachIndexed { index, formatDiscogs ->
        val name = formatDiscogs.name ?: ""
        val quantity = formatDiscogs.qty
        val text = formatDiscogs.text ?: ""
        val descriptionsString = formatDiscogs.descriptions?.joinToString(" | ") ?: ""

        val descriptionText = when {
            name.isNotEmpty() && descriptionsString.isEmpty() && text.isEmpty() -> name
            name.isEmpty() && descriptionsString.isEmpty() && text.isNotEmpty() ->
                "${context.getString(R.string.media_undefined)} : $text"

            name.isEmpty() && descriptionsString.isNotEmpty() && text.isEmpty() ->
                "${context.getString(R.string.media_undefined)} : $descriptionsString"

            name.isNotEmpty() && descriptionsString.isEmpty() && text.isNotEmpty() -> "$name : $text"
            name.isNotEmpty() && descriptionsString.isNotEmpty() && text.isEmpty() -> "$name : $descriptionsString"
            name.isEmpty() && descriptionsString.isNotEmpty() && text.isNotEmpty() ->
                "${context.getString(R.string.media_undefined)} : $descriptionsString , $text"

            name.isNotEmpty() && descriptionsString.isNotEmpty() && text.isNotEmpty() -> "$name : $descriptionsString , $text"
            else -> ""
        }

        val quantityDescriptionText = when (quantity) {
            null -> descriptionText.ifEmpty { "" }
            "1" -> descriptionText.ifEmpty { "" }
            else -> if (descriptionText.isEmpty()) "" else "$quantity x $descriptionText"
        }

        myFormat += when (index) {
            listFormatsSize -> quantityDescriptionText.ifEmpty { "" }
            else -> if (quantityDescriptionText.isEmpty()) "" else "$quantityDescriptionText\n"
        }

        if (name.isNotEmpty()) listFormat.add(name)
    }

    myFormat = when {
        formatQuantity > 0 && myFormat.isNotEmpty() ->
            "$formatQuantity ${context.getString(R.string.media)}\n$myFormat"

        formatQuantity > 0 && myFormat.isEmpty() ->
            "$formatQuantity ${context.getString(R.string.media)}"

        formatQuantity < 1 && myFormat.isNotEmpty() -> myFormat
        else -> ""
    }

    val formatMedia = listFormat.toSet().toList().joinToString(" ").trim()
    val artistNameAndTitleSeparate = result.title?.split(" - ")?.map { string ->
        string.noSimpleQuoteWhiteSpace()
    }

    return Disc(
        name = artistNameAndTitleSeparate!![0].removeParenthesesDigit(),
        title = artistNameAndTitleSeparate[1],
        year = result.year ?: "",
        country = result.country ?: "",
        format = myFormat,
        formatMedia = formatMedia,
        coverImage = result.coverImage ?: "",
        barcode = barcodeDiscogs,
        idDisc = result.id!!,
        addBy = AddBy.NONE
    )
}