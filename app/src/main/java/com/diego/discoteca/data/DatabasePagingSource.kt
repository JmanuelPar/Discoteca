package com.diego.discoteca.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diego.discoteca.activity.DiscotecaApplication
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscDb
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.MANUALLY
import com.diego.discoteca.util.SCAN
import com.diego.discoteca.util.SEARCH
import com.diego.discoteca.util.stringNormalizeDatabase

private const val DATABASE_STARTING_PAGE_INDEX = 0

class DatabasePagingSourceBarcode(
    private val repository: DiscRepository,
    private val barcode: String
) : PagingSource<Int, Disc>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Disc> {
        val page = params.key ?: DATABASE_STARTING_PAGE_INDEX

        return try {
            // Get list of disc in database with a barcode
            val listBarcode = repository.getPagedListBarcode(
                barcode = barcode,
                limit = params.loadSize,
                offset = page * params.loadSize
            )
            //val listBarcode = emptyList<Disc>()
            /* A disc can have one or more versions for the same barcode
            Different artist/group can have the same barcode for the disc */

            /* Create a list with artist/group name + title + year with
            for list of disc in database with a barcode */
            val list = listBarcode.map { disc ->
                DiscDb(
                    0,
                    disc.name.stringNormalizeDatabase(),
                    disc.title.stringNormalizeDatabase(),
                    disc.year
                )
            }.toSet()

            /* Get list of disc in database added manually / by search by the user
            with artist/group name + title + name  */
            val listDbManuallySearch = mutableListOf<Disc>()
            if (list.isNotEmpty()) {
                list.forEach { discDb ->
                    val listDB = repository.getListDiscDbManuallySearch(
                        discDb.name,
                        discDb.title,
                        discDb.year
                    )

                   // val listDB = emptyList<Disc>()

                    listDbManuallySearch += listDB
                }
            }

            val listDiscDb = listBarcode + listDbManuallySearch

            val prevKey = when (page) {
                DATABASE_STARTING_PAGE_INDEX -> null
                else -> page - 1
            }
            val nextKey = when {
                listDiscDb.isEmpty() -> null
                else -> page + 1
            }

            LoadResult.Page(
                data = listDiscDb.sortedWith(
                    compareBy<Disc> { it.addBy == SCAN }
                        .thenBy { it.addBy == SEARCH }
                        .thenBy { it.addBy == MANUALLY }
                        .reversed()
                ),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Disc>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
