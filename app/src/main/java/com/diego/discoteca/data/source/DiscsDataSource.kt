package com.diego.discoteca.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.CountFormatMedia
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import kotlinx.coroutines.flow.Flow

interface DiscsDataSource {

    val countAllDiscs: Flow<Int>

    val countFormatMediaList: Flow<List<CountFormatMedia>>

    fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<DatabaseDisc>>

    fun getDiscWithId(discId: Long): LiveData<DatabaseDisc>

    suspend fun insertLong(databaseDisc: DatabaseDisc): Long

    suspend fun update(databaseDisc: DatabaseDisc)

    suspend fun deleteById(discId: Long)

    suspend fun getListDiscDbPresent(name: String, title: String, year: String): List<DatabaseDisc>

    fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>>

    fun getSearchDiscStream(
        discPresent: DiscPresent
    ): Flow<PagingData<Disc>>

    fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>>
}