package com.diego.discoteca

import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.util.AddBy
import java.util.concurrent.atomic.AtomicInteger

class DiscApiFactory {
    private val counter = AtomicInteger(0)

    fun createDiscApi(): Disc {
        val id = counter.incrementAndGet()
        return Disc(
            name = "name_$id",
            title = "title_$id",
            year = "year_$id",
            country = "country_$id",
            format = "format_$id",
            formatMedia = "formatMedia_$id",
            coverImage = "url_coverImage_$id",
            barcode = "barcode_$id",
            idDisc = id + 1,
            addBy = AddBy.NONE
        )
    }
}