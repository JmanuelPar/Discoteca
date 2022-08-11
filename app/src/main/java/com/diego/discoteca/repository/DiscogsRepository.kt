package com.diego.discoteca.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diego.discoteca.data.DatabasePagingSourceBarcode
import com.diego.discoteca.data.DiscogsPagingSourceSearchBarcode
import com.diego.discoteca.data.DiscogsPagingSourceSearchDisc
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscPresent
import com.diego.discoteca.network.DiscogsApiService
import kotlinx.coroutines.flow.Flow

class DiscogsRepository(private val service: DiscogsApiService) {

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
                    discPresent = discPresent
                )
            }
        ).flow
    }

    fun getSearchBarcodeStream(
        repository: DiscRepository,
        barcode: String,
    ): Flow<PagingData<Disc>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_DISCOGS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DiscogsPagingSourceSearchBarcode(
                    service = service,
                    repository = repository,
                    barcode = barcode
                )
            }
        ).flow
    }

    fun getSearchBarcodeDatabase(
        repository: DiscRepository,
        barcode: String,
    ): Flow<PagingData<Disc>> {
        return Pager(
            config = PagingConfig(
                pageSize = DATABASE_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DatabasePagingSourceBarcode(
                    repository = repository,
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