package com.diego.discoteca.domain

import android.os.Parcelable
import com.diego.discoteca.model.DiscLight
import kotlinx.parcelize.Parcelize

@Parcelize
data class Disc(
    var id: Long,
    val name: String,
    val title: String,
    var year: String,
    val country: String,
    var format: String,
    var formatMedia: String,
    val coverImage: String,
    var barcode: String,
    val idDisc: Int,
    var isPresentByManually: Boolean? = false,
    var isPresentByScan: Boolean? = false,
    var isPresentBySearch: Boolean? = false,
    var addBy: Int,
    var discLight: DiscLight? = DiscLight(
        id = 0L,
        name = "",
        title = "",
        year = "",
        country = "",
        format = "",
        formatMedia = ""
    )
) : Parcelable {

    constructor(
        id: Long,
        name: String,
        title: String,
        year: String,
        country: String,
        format: String,
        formatMedia: String,
        coverImage: String,
        barcode: String,
        addBy: Int
    ) :
            this(
                id = id,
                name = name,
                title = title,
                year = year,
                country = country,
                format = format,
                formatMedia = formatMedia,
                coverImage = coverImage,
                barcode = barcode,
                idDisc = -1,
                addBy = addBy
            )

    constructor(
        name: String,
        title: String,
        year: String,
        country: String,
        format: String,
        formatMedia: String,
        coverImage: String,
        barcode: String,
        idDisc: Int,
        addBy: Int
    ) :
            this(
                id = 0L,
                name = name,
                title = title,
                year = year,
                country = country,
                format = format,
                formatMedia = formatMedia,
                coverImage = coverImage,
                barcode = barcode,
                idDisc = idDisc,
                addBy = addBy
            )

    constructor(
        name: String,
        title: String,
        year: String,
        format: String,
        barcode: String,
        coverImage: String,
        idDisc: Int,
        addBy: Int
    ) :
            this(
                id = 0L,
                name = name,
                title = title,
                year = year,
                country = "",
                format = format,
                formatMedia = "",
                coverImage = coverImage,
                barcode = barcode,
                idDisc = idDisc,
                addBy = addBy
            )

    constructor(
        name: String,
        title: String,
        year: String,
        addBy: Int
    ) :
            this(
                id = 0L,
                name = name,
                title = title,
                year = year,
                country = "",
                format = "",
                formatMedia = "",
                coverImage = "",
                barcode = "",
                idDisc = -1,
                addBy = addBy
            )

    override fun toString(): String {
        return "\n Disc ----> \n id : $id" +
                "\n idDisc : $idDisc" +
                "\n name : $name" +
                "\n title : $title" +
                "\n year : $year" +
                "\n country : $country" +
                "\n format : $format" +
                "\n formatMedia : $formatMedia" +
                "\n barcode : $barcode" +
                "\n addBy : $addBy" +
                "\n isPresentManually : $isPresentByManually " +
                "\n isPresentScan : $isPresentByScan " +
                "\n idPresentSearch : $isPresentBySearch " +
                "\n discLight : $discLight"
    }
}





