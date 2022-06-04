package com.diego.discoteca.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diego.discoteca.R
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.Result
import com.diego.discoteca.util.*

/* addBy: Int ->
   1 : add by manually
   2 : add by scan
   3 : add by search */

@Entity(tableName = "disc_table")
data class DatabaseDisc(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "year")
    val year: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "format")
    val format: String,

    @ColumnInfo(name = "formatMedia")
    val formatMedia: String,

    @ColumnInfo(name = "coverImage")
    val coverImage: String,

    @ColumnInfo(name = "barcode")
    val barcode: String,

    @ColumnInfo(name = "idDisc")
    val idDisc: Int,

    @ColumnInfo(name = "addBy")
    val addBy: Int,

    @ColumnInfo(name = "nameNormalize")
    val nameNormalize: String,

    @ColumnInfo(name = "titleNormalize")
    val titleNormalize: String
)

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

fun List<Result>.asDomainModel(barcode: String): List<Disc> {
    return map { result ->
        processingResult(result, barcode)
    }
}

private fun processingResult(result: Result, barcode: String): Disc {
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
                "${MyApp.res.getString(R.string.media_undefined)} : $text"
            name.isEmpty() && descriptionsString.isNotEmpty() && text.isEmpty() ->
                "${MyApp.res.getString(R.string.media_undefined)} : $descriptionsString"
            name.isNotEmpty() && descriptionsString.isEmpty() && text.isNotEmpty() -> "$name : $text"
            name.isNotEmpty() && descriptionsString.isNotEmpty() && text.isEmpty() -> "$name : $descriptionsString"
            name.isEmpty() && descriptionsString.isNotEmpty() && text.isNotEmpty() ->
                "${MyApp.res.getString(R.string.media_undefined)} : $descriptionsString , $text"
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
            "$formatQuantity ${MyApp.res.getString(R.string.media)}\n$myFormat"
        formatQuantity > 0 && myFormat.isEmpty() ->
            "$formatQuantity ${MyApp.res.getString(R.string.media)}"
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
        addBy = -1
    )
}

