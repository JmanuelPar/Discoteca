package com.diego.discoteca.repository

import androidx.lifecycle.map
import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.data.source.DiscsDataSource
import com.diego.discoteca.util.asDatabaseModel
import com.diego.discoteca.util.asDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultDiscsRepository(
    private val discsLocalDataSource: DiscsDataSource,
    private val discsPagingDataSource: DiscsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiscsRepository {

    override val countAllDiscs = discsLocalDataSource.countAllDiscs

    override val countFormatMediaList = discsLocalDataSource.countFormatMediaList

    override fun getAllDiscs(searchQuery: String, sortOrder: SortOrder) =
        discsLocalDataSource.getAllDiscs(searchQuery, sortOrder).map {
            it.asDomainModel()
        }

    override fun getDiscWithId(discId: Long) =
        discsLocalDataSource.getDiscWithId(discId).map {
            it.asDomainModel()
        }

    override suspend fun insertLong(disc: Disc) = withContext(ioDispatcher) {
        discsLocalDataSource.insertLong(disc.asDatabaseModel())
    }

    override suspend fun update(disc: Disc) = withContext(ioDispatcher) {
        discsLocalDataSource.update(disc.asDatabaseModel())
    }

    override suspend fun deleteById(discId: Long) = withContext(ioDispatcher) {
        discsLocalDataSource.deleteById(discId)
    }

    override suspend fun getListDiscDbPresent(name: String, title: String, year: String) =
        withContext(ioDispatcher) {
            discsLocalDataSource.getListDiscDbPresent(
                name = name,
                title = title,
                year = year
            ).asDomainModel()
        }

    override fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>> {
        return discsPagingDataSource.getSearchBarcodeStream(barcode)
    }

    override fun getSearchDiscStream(
        discPresent: DiscPresent
    ): Flow<PagingData<Disc>> {
        return discsPagingDataSource.getSearchDiscStream(discPresent)
    }

    override fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>> {
        return discsPagingDataSource.getSearchBarcodeDatabase(barcode)
    }
}
