package com.diego.discoteca.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.database.asDatabaseModel
import com.diego.discoteca.database.asDomainModel
import com.diego.discoteca.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class DiscRepository(private val dao: DiscDatabaseDao) {

    fun getCountAllDiscs() = dao.countAllDiscs()

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

    fun getCountFormatMediaList() = dao.getCountFormatMediaList()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertLong(disc: Disc) = dao.insertLong(disc.asDatabaseModel())

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(disc: Disc) {
        dao.update(disc.asDatabaseModel())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteById(discId: Long) {
        dao.deleteById(discId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getPagedListBarcode(barcode: String, limit: Int, offset: Int) =
        dao.getPagedListBarcode(barcode, limit, offset).asDomainModel()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getListDiscDbScan(barcode: String) =
        dao.getListDiscDbScan(barcode).asDomainModel()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDiscDbManually(name: String, title: String, year: String) =
        dao.getDiscDbManually(
            name.stringNormalizeDatabase(),
            title.stringNormalizeDatabase(),
            year
        )?.asDomainModel()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDiscDbSearch(idDisc: Int, name: String, title: String, year: String) =
        dao.getDiscDbSearch(
            idDisc,
            name,
            title,
            year
        )?.asDomainModel()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getListDiscDbPresent(name: String, title: String, year: String) =
        dao.getListDiscDbPresent(
            name.stringNormalizeDatabase(),
            title.stringNormalizeDatabase(),
            year
        ).asDomainModel()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getListDiscDbManuallySearch(name: String, title: String, year: String) =
        dao.getListDiscDbManuallySearch(
            name,
            title,
            year
        ).asDomainModel()

    companion object {
        const val DATABASE_PAGE_SIZE = 20
    }
}
