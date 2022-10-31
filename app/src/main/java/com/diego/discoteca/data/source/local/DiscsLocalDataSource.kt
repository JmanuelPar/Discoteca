package com.diego.discoteca.data.source.local

import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.CountFormatMedia
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.data.source.DiscsDataSource
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.util.stringNormalizeDatabase
import kotlinx.coroutines.flow.Flow

class DiscsLocalDataSource internal constructor(
    private val dao: DiscDatabaseDao
) : DiscsDataSource {

    override val countAllDiscs: Flow<Int> = dao.countAllDiscs()

    override val countFormatMediaList: Flow<List<CountFormatMedia>> = dao.getCountFormatMediaList()

    override fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<DatabaseDisc>> {
        return when (sortOrder) {
            SortOrder.BY_NAME -> dao.getAllDiscsByName(searchQuery)
            SortOrder.BY_TITLE -> dao.getAllDiscsByTitle(searchQuery)
            SortOrder.BY_YEAR -> dao.getAllDiscsByYear(searchQuery)
        }
    }

    override fun getDiscWithId(discId: Long) = dao.getDiscWithId(discId)

    override suspend fun insertLong(databaseDisc: DatabaseDisc) = dao.insertLong(databaseDisc)

    override suspend fun update(databaseDisc: DatabaseDisc) = dao.update(databaseDisc)

    override suspend fun deleteById(discId: Long) = dao.deleteById(discId)

    override suspend fun getListDiscDbPresent(name: String, title: String, year: String) =
        dao.getListDiscDbPresent(
            name = name.stringNormalizeDatabase(),
            title = title.stringNormalizeDatabase(),
            year = year
        )

    override fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>> {
        TODO("Not required for the local data source")
    }

    override fun getSearchDiscStream(discPresent: DiscPresent): Flow<PagingData<Disc>> {
        TODO("Not required for the local data source")
    }

    override fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>> {
        TODO("Not required for the local data source")
    }
}