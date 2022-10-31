package com.diego.discoteca

import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import java.util.concurrent.atomic.AtomicInteger

class DatabaseDiscFactory {
    private val counter = AtomicInteger(0)

    fun createDatabaseDisc(addBy: AddBy): DatabaseDisc {
        val id = counter.incrementAndGet()

        return when (addBy) {
            AddBy.MANUALLY -> {
                DatabaseDisc(
                    id = id.toLong(),
                    name = "name_3",
                    title = "title_3",
                    year = "year_3",
                    country = "",
                    format = "",
                    formatMedia = "",
                    coverImage = "",
                    barcode = "",
                    idDisc = -1,
                    addBy = AddBy.MANUALLY,
                    nameNormalize = "name_normalize_3",
                    titleNormalize = "title_normalize_3"
                )
            }
            AddBy.SCAN -> {
                DatabaseDisc(
                    id = id.toLong(),
                    name = "name_1",
                    title = "title_1",
                    year = "year_1",
                    country = "country_1",
                    format = "format_1",
                    formatMedia = "format_media_1",
                    coverImage = "url_coverImage_1",
                    barcode = "barcode_1",
                    idDisc = 2,
                    addBy = AddBy.SCAN,
                    nameNormalize = "name_normalize_1",
                    titleNormalize = "title_normalize_1"
                )
            }
            //AddBy.SEARCH
            else -> {
                DatabaseDisc(
                    id = id.toLong(),
                    name = "name_2",
                    title = "title_2",
                    year = "year_2",
                    country = "country_2",
                    format = "format_2",
                    formatMedia = "format_media_2",
                    coverImage = "url_coverImage_2",
                    barcode = "",
                    idDisc = 3,
                    addBy = AddBy.SEARCH,
                    nameNormalize = "name_normalize_2",
                    titleNormalize = "title_normalize_2"
                )
            }
        }
    }
}