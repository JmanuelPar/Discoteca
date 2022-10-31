package com.diego.discoteca.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.CountFormatMedia
import com.diego.discoteca.data.model.DiscPresent
import kotlinx.coroutines.flow.Flow

interface DiscsRepository {

    val countAllDiscs: Flow<Int>

    val countFormatMediaList: Flow<List<CountFormatMedia>>

    fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<Disc>>

    fun getDiscWithId(discId: Long): LiveData<Disc>

    suspend fun insertLong(disc: Disc): Long

    suspend fun update(disc: Disc)

    suspend fun deleteById(discId: Long)

    suspend fun getListDiscDbPresent(name: String, title: String, year: String): List<Disc>

    fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>>

    fun getSearchDiscStream(
        discPresent: DiscPresent
    ): Flow<PagingData<Disc>>

    fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>>
}
