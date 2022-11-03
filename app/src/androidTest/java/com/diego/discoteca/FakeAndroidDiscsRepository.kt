package com.diego.discoteca

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.CountFormatMedia
import com.diego.discoteca.data.model.DiscDb
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.asDatabaseModel
import com.diego.discoteca.util.asDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAndroidDiscsRepository : DiscsRepository {

    private var discDatabaseItems = mutableListOf<DatabaseDisc>()
    private lateinit var discApiItems: List<Disc>

    private val observableDisc = MutableLiveData<Disc>()

    override val countAllDiscs: Flow<Int> =
        flow {
            emit(discDatabaseItems.size)
        }

    override val countFormatMediaList: Flow<List<CountFormatMedia>> =
        flow {
            val countFormatMediaItems = discDatabaseItems.groupBy { databaseDisc ->
                databaseDisc.formatMedia
            }.map {
                CountFormatMedia(
                    it.value.first().id,
                    it.value.size.toString(),
                    it.key
                )
            }

            emit(countFormatMediaItems)
        }

    override fun getAllDiscs(searchQuery: String, sortOrder: SortOrder): Flow<List<Disc>> {
        return flow {
            val allDiscs = when (sortOrder) {
                SortOrder.BY_NAME -> discDatabaseItems.sortedBy {
                    it.name
                }
                SortOrder.BY_TITLE -> discDatabaseItems.sortedBy {
                    it.title
                }
                SortOrder.BY_YEAR -> discDatabaseItems.sortedBy {
                    it.year
                }
            }.asDomainModel()

            emit(allDiscs)
        }
    }

    override fun getDiscWithId(discId: Long): LiveData<Disc> {
        val disc = discDatabaseItems.find { disc ->
            disc.id == discId
        }!!.asDomainModel()
        observableDisc.value = disc
        return observableDisc
    }

    override suspend fun insertLong(disc: Disc): Long {
        val discDatabase = disc.asDatabaseModel()
        discDatabaseItems.add(discDatabase)
        val index = discDatabaseItems.indexOf(discDatabase)
        return (index + 1).toLong()
    }

    override suspend fun update(disc: Disc) {
        val discDatabase = discDatabaseItems.find { it.id == disc.id }
        val index = discDatabaseItems.indexOf(discDatabase)
        discDatabaseItems[index] = disc.asDatabaseModel()
    }

    override suspend fun deleteById(discId: Long) {
        discDatabaseItems.removeIf { disc ->
            disc.id == discId
        }
    }

    override suspend fun getListDiscDbPresent(
        name: String,
        title: String,
        year: String
    ): List<Disc> {
        val listDiscDbPresent = discDatabaseItems.filter { disc ->
            (disc.nameNormalize == name || disc.titleNormalize == title) && disc.year == year
        }.asDomainModel()
        return listDiscDbPresent
    }

    override fun getSearchBarcodeStream(barcode: String): Flow<PagingData<Disc>> =
        flow {
            val searchBarcode = discApiItems.filter {
                it.barcode == barcode
            }

            val listDb = if (searchBarcode.isNotEmpty()) {
                // We simplify
                val discDb = searchBarcode.map {
                    DiscDb(
                        idDisc = it.idDisc,
                        name = it.name,
                        title = it.title,
                        year = it.year
                    )
                }.first()

                val listDbManually = discDatabaseItems.filter {
                    it.name == discDb.name &&
                            it.title == discDb.title &&
                            it.year == discDb.year &&
                            it.addBy == AddBy.MANUALLY
                }.asDomainModel()

                val listDbSearch = discDatabaseItems.filter {
                    it.idDisc == discDb.idDisc &&
                            it.name == discDb.name &&
                            it.title == discDb.title &&
                            it.year == discDb.year &&
                            it.addBy == AddBy.SEARCH
                }.asDomainModel()

                val listDbScan = discDatabaseItems.filter {
                    it.barcode == barcode
                }.asDomainModel()

                listDbManually + listDbSearch + listDbScan
            } else {
                searchBarcode
            }

            if (listDb.isNotEmpty() && searchBarcode.isNotEmpty()) {
                searchBarcode.apply {
                    map { discApi ->
                        listDb.map { discDb ->
                            when {
                                discApi.idDisc == discDb.idDisc && discDb.addBy == AddBy.SCAN ->
                                    discApi.isPresentByScan = true
                                discApi.idDisc == discDb.idDisc && discDb.addBy == AddBy.SEARCH -> {
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
                                && discDb.addBy == AddBy.MANUALLY
                            ) {
                                val discApiName = discApi.name
                                val discApiTitle = discApi.title
                                val discApiYear = discApi.year
                                if ((discApiName == discDb.name
                                            || discApiTitle == discDb.title)
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

            val pagingData = PagingData.from(searchBarcode)
            emit(pagingData)
        }

    override fun getSearchDiscStream(discPresent: DiscPresent): Flow<PagingData<Disc>> =
        flow {
            val searchDisc = discApiItems.filter {
                it.name == discPresent.discAdd.name &&
                        it.title == discPresent.discAdd.title &&
                        it.year == discPresent.discAdd.year
            }

            val listDb = discPresent.list
            val discAdd = discPresent.discAdd

            if (listDb.isNotEmpty() && searchDisc.isNotEmpty()) {
                searchDisc.apply {
                    map { discApi ->
                        listDb.map { discDb ->
                            when {
                                discApi.idDisc == discDb.idDisc && discDb.addBy == AddBy.SCAN ->
                                    discApi.isPresentByScan = true
                                discApi.idDisc == discDb.idDisc && discDb.addBy == AddBy.SEARCH ->
                                    discApi.isPresentBySearch = true
                            }
                        }
                    }

                    map { discApi ->
                        if (discApi.isPresentByScan == false
                            && discApi.isPresentBySearch == false
                            && discAdd.addBy == AddBy.MANUALLY
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

            val pagingData = PagingData.from(searchDisc)
            emit(pagingData)
        }

    override fun getSearchBarcodeDatabase(barcode: String): Flow<PagingData<Disc>> =
        flow {
            val searchBarcodeDatabase = discDatabaseItems.filter {
                it.barcode == barcode
            }

            val list = searchBarcodeDatabase.map { disc ->
                DiscDb(
                    idDisc = 0,
                    name = disc.nameNormalize,
                    title = disc.titleNormalize,
                    year = disc.year
                )
            }.toSet()

            val listDbManuallySearch = mutableListOf<Disc>()
            if (list.isNotEmpty()) {
                list.forEach { discDb ->
                    val listDb = discDatabaseItems.filter {
                        it.nameNormalize == discDb.name &&
                                it.titleNormalize == discDb.title &&
                                it.year == discDb.year &&
                                (it.addBy == AddBy.MANUALLY ||
                                        it.addBy == AddBy.SEARCH)
                    }.asDomainModel()
                    listDbManuallySearch += listDb
                }
            }

            val listDiscDb = searchBarcodeDatabase.asDomainModel() + listDbManuallySearch
            val pagingData = PagingData.from(listDiscDb)
            emit(pagingData)
        }

    fun setDiscApi(list: List<Disc>) {
        discApiItems = list
    }

    fun setDatabaseDisc(list: List<DatabaseDisc>) {
        discDatabaseItems.addAll(list)
    }
}