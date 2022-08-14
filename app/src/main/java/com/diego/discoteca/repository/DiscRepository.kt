package com.diego.discoteca.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diego.discoteca.data.DatabasePagingSourceBarcode
import com.diego.discoteca.data.DiscogsPagingSourceSearchBarcode
import com.diego.discoteca.data.DiscogsPagingSourceSearchDisc
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.database.asDatabaseModel
import com.diego.discoteca.database.asDomainModel
import com.diego.discoteca.model.CountFormatMedia
import com.diego.discoteca.model.DiscPresent
import com.diego.discoteca.network.DiscogsApiService
import com.diego.discoteca.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DiscRepository(
    private val dao: DiscDatabaseDao,
    private val service: DiscogsApiService,
    private val context: Context
) {

    val countAllDiscs: Flow<Int> = dao.countAllDiscs()

    val countFormatMediaList: Flow<List<CountFormatMedia>> = dao.getCountFormatMediaList()

    fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<Disc>> {
        return when (sortOrder) {
            SortOrder.BY_NAME -> dao.getAllDiscsByName(searchQuery).map {
                it.asDomainModel()
            }
            SortOrder.BY_TITLE -> dao.getAllDiscsByTitle(searchQuery).map {
                it.asDomainModel()
            }
            SortOrder.BY_YEAR -> dao.getAllDiscsByYear(searchQuery).map {
                it.asDomainModel()
            }
        }
    }

    fun getDiscWithId(key: Long) = Transformations.map(dao.getDiscWithId(key)) {
        it.asDomainModel()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertLong(disc: Disc) = withContext(Dispatchers.IO) {
        dao.insertLong(disc.asDatabaseModel())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(disc: Disc) = withContext(Dispatchers.IO) {
        dao.update(disc.asDatabaseModel())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteById(discId: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(discId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getListDiscDbPresent(name: String, title: String, year: String) =
        withContext(Dispatchers.IO) {
            dao.getListDiscDbPresent(
                name = name.stringNormalizeDatabase(),
                title = title.stringNormalizeDatabase(),
                year = year
            ).asDomainModel()
        }

    fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>> {
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

    fun getSearchDiscStream(
        discPresent: DiscPresent
    ): Flow<PagingData<Disc>> {
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

    fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>> {
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

    companion object {
        const val NETWORK_DISCOGS_PAGE_SIZE = 50
        const val DATABASE_PAGE_SIZE = 20
    }
}
