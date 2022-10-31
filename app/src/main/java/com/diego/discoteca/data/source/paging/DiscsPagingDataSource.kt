package com.diego.discoteca.data.source.paging

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.CountFormatMedia
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.data.source.DiscsDataSource
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.network.DiscogsApiService
import com.diego.discoteca.util.Constants.DATABASE_PAGE_SIZE
import com.diego.discoteca.util.Constants.NETWORK_DISCOGS_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class DiscsPagingDataSource internal constructor(
    private val dao: DiscDatabaseDao,
    private val service: DiscogsApiService,
    private val context: Context
) : DiscsDataSource {

    override val countAllDiscs: Flow<Int>
        get() = TODO("Not required for the paging data source")

    override val countFormatMediaList: Flow<List<CountFormatMedia>>
        get() = TODO("Not required for the paging data source")

    override fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<DatabaseDisc>> {
        TODO("Not required for the paging data source")
    }

    override fun getDiscWithId(discId: Long): LiveData<DatabaseDisc> {
        TODO("Not required for the paging data source")
    }

    override suspend fun insertLong(databaseDisc: DatabaseDisc): Long {
        TODO("Not required for the paging data source")
    }

    override suspend fun update(databaseDisc: DatabaseDisc) {
        // Not required for the paging data source
    }

    override suspend fun deleteById(discId: Long) {
        // Not required for the paging data source
    }

    override suspend fun getListDiscDbPresent(
        name: String,
        title: String,
        year: String
    ): List<DatabaseDisc> {
        TODO("Not required for the paging data source")
    }

    override fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_DISCOGS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DiscogsPagingSourceSearchBarcode(
                    service = service,
                    dao = dao,
                    context = context,
                    barcode = barcode,
                )
            }
        ).flow
    }

    override fun getSearchDiscStream(discPresent: DiscPresent): Flow<PagingData<Disc>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_DISCOGS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DiscogsPagingSourceSearchDisc(
                    service = service,
                    context = context,
                    discPresent = discPresent
                )
            }
        ).flow
    }

    override fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>> {
        return Pager(
            config = PagingConfig(
                pageSize = DATABASE_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DatabasePagingSourceBarcode(
                    dao = dao,
                    barcode = barcode
                )
            }
        ).flow
    }
}