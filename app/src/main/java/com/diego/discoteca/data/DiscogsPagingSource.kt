package com.diego.discoteca.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diego.discoteca.BuildConfig
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.database.asDomainModel
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.*
import com.diego.discoteca.network.DiscogsApiService
import com.diego.discoteca.repository.DiscogsRepository.Companion.NETWORK_DISCOGS_PAGE_SIZE
import com.diego.discoteca.util.*
import retrofit2.HttpException
import java.io.IOException

private const val DISCOGS_STARTING_PAGE_INDEX = 0

class DiscogsPagingSourceSearchBarcode(
    private val service: DiscogsApiService,
    private val barcode: String
) : PagingSource<Int, Disc>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Disc> {
        val page = params.key ?: DISCOGS_STARTING_PAGE_INDEX

        return try {
            val response = service.getSearchBarcode(
                type = "release",
                barcode = barcode,
                page = page,
                itemsPerPage = params.loadSize,
                key = BuildConfig.API_KEY,
                secret = BuildConfig.API_SECRET
            )

            /* if response.pagination.pages don't work, we take page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE
            but with page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE
            we can have a HTTP 404 error when there is no more page to load

            We don't think we can have more than 150 (3 * params.loadSize) results for a barcode
            Always page = 0 and discogsPagesTotal = 1 */
            val discogsPagesTotal =
                response.pagination?.pages ?: (page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE))

            /* A disc can have one or more versions for the same barcode
            Different artist/group can have the same barcode for the disc */

            /* Get list of disc in database with barcode scanned by the user,
            added by scan (none, one or more) */
            val repository = MyApp.instance.repository
            val listDbScan = repository.getListDiscDbScan(barcode)

            val listApi = response.results?.asDomainModel(barcode)?.sortedBy {
                it.country.lowercase()
            }

            /* Get list of disc in database, added manually by the user
            with name artist/group + title + year in list Discogs API (none, one or more) */
            val listDbManually = mutableListOf<Disc>()

            /* Get list of disc in database, added by search by the user
            with name artist/group + title + year in list Discogs API (none, one or more) */
            val listDbSearch = mutableListOf<Disc>()

            // Create a list of disc with idDisc + artist/group + title + year from list Discogs Api
            val list = listApi?.map { discApi ->
                DiscDb(
                    discApi.idDisc,
                    discApi.name,
                    discApi.title,
                    discApi.year
                )
            }

            list?.forEach { discDb ->
                // Disc added by the user manually
                val discDbManually = repository.getDiscDbManually(
                    discDb.name,
                    discDb.title,
                    discDb.year
                )
                discDbManually?.let { listDbManually.add(it) }

                val discDbSearch = repository.getDiscDbSearch(
                    discDb.idDisc,
                    discDb.name,
                    discDb.title,
                    discDb.year
                )
                discDbSearch?.let { listDbSearch.add(it) }
            }

            val listDb = listDbManually.toSet() + listDbScan + listDbSearch
            if (listDb.isNotEmpty() && listApi?.isNotEmpty() == true) {
                listApi.apply {
                    map { discApi ->
                        listDb.map { discDb ->
                            when {
                                discApi.idDisc == discDb.idDisc && discDb.addBy == SCAN ->
                                    discApi.isPresentByScan = true
                                discApi.idDisc == discDb.idDisc && discDb.addBy == SEARCH -> {
                                    discApi.isPresentBySearch = true
                                    discApi.discLight = DiscLight(
                                        id = discDb.id,
                                        name = discDb.name,
                                        title = discDb.title,
                                        year = discDb.year,
                                        country = discDb.country,
                                        format = discDb.format,
                                        formatMedia = discDb.formatMedia
                                    )
                                }
                            }
                        }
                    }

                    map { discApi ->
                        listDb.map { discDb ->
                            if (discApi.isPresentByScan == false
                                && discApi.isPresentBySearch == false
                                && discDb.addBy == MANUALLY
                            ) {
                                val discApiName = discApi.name.stringNormalizeDatabase()
                                val discApiTitle = discApi.title.stringNormalizeDatabase()
                                val discApiYear = discApi.year
                                if (discApiName.contains(discDb.name.stringNormalizeDatabase())
                                    || discApiTitle.contains(discDb.title.stringNormalizeDatabase())
                                    && discApiYear == discDb.year
                                ) {
                                    discApi.isPresentByManually = true
                                    discApi.discLight = DiscLight(
                                        id = discDb.id,
                                        name = discDb.name,
                                        title = discDb.title,
                                        year = discDb.year,
                                        country = "",
                                        format = "",
                                        formatMedia = ""
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val prevKey = when (page) {
                DISCOGS_STARTING_PAGE_INDEX -> null
                else -> page - 1
            }

            val nextKey = when {
                page == DISCOGS_STARTING_PAGE_INDEX && discogsPagesTotal == 1 -> null
                page < discogsPagesTotal -> page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE)
                else -> null
            }

            LoadResult.Page(
                data = listApi!!.sortedWith(
                    compareBy<Disc> { it.isPresentBySearch == true }
                        .thenBy { it.isPresentByScan == true }
                        .reversed()
                ),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Disc>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

class DiscogsPagingSourceSearchDisc(
    private val service: DiscogsApiService,
    private val discPresent: DiscPresent
) : PagingSource<Int, Disc>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Disc> {
        val page = params.key ?: DISCOGS_STARTING_PAGE_INDEX
        val apiQueryName = discPresent.discAdd.name
        val apiQueryReleaseTitle = discPresent.discAdd.title
        val apiQueryYear = discPresent.discAdd.year

        return try {
            val response = service.getSearchDisc(
                type = "release",
                artist = apiQueryName,
                release_title = apiQueryReleaseTitle,
                year = apiQueryYear,
                page = page,
                itemsPerPage = params.loadSize,
                key = BuildConfig.API_KEY,
                secret = BuildConfig.API_SECRET
            )

            /* if response.pagination.pages don't work, we take page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE
            but with page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE
            we can have a HTTP 404 error when there is no more page to load */
            val discogsPagesTotal =
                response.pagination?.pages ?: (page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE))

            val listDb = discPresent.list
            val discAdd = discPresent.discAdd
            val listApi = response.results?.asDomainModel("")?.sortedWith(
                compareBy({ it.country.lowercase() }, { it.format.lowercase() }
                ))

            if (listDb.isNotEmpty() && listApi?.isNotEmpty() == true) {
                listApi.apply {
                    map { discApi ->
                        listDb.map { discDb ->
                            when {
                                discApi.idDisc == discDb.idDisc && discDb.addBy == SCAN ->
                                    discApi.isPresentByScan = true
                                discApi.idDisc == discDb.idDisc && discDb.addBy == SEARCH ->
                                    discApi.isPresentBySearch = true
                            }
                        }
                    }

                    map { discApi ->
                        if (discApi.isPresentByScan == false
                            && discApi.isPresentBySearch == false
                            && discAdd.addBy == MANUALLY
                        ) {
                            discApi.isPresentByManually = true
                            discApi.discLight = DiscLight(
                                id = discAdd.id,
                                name = discAdd.name,
                                title = discAdd.title,
                                year = discAdd.year,
                                country = "",
                                format = "",
                                formatMedia = ""
                            )
                        }
                    }
                }
            }

            val prevKey = when (page) {
                DISCOGS_STARTING_PAGE_INDEX -> null
                else -> page - 1
            }

            val nextKey = when {
                page == DISCOGS_STARTING_PAGE_INDEX && discogsPagesTotal == 1 -> null
                page < discogsPagesTotal -> page + (params.loadSize / NETWORK_DISCOGS_PAGE_SIZE)
                else -> null
            }

            LoadResult.Page(
                data = listApi!!.sortedWith(
                    compareBy<Disc> { it.isPresentBySearch == true }
                        .thenBy { it.isPresentByScan == true }
                        .reversed()
                ),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
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