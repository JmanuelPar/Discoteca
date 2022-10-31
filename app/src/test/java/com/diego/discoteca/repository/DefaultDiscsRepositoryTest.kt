package com.diego.discoteca.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import com.diego.discoteca.*
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultDiscsRepositoryTest {

    private lateinit var listDiscApi: List<Disc>
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var discsRepository: DefaultDiscsRepository
    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc
    private lateinit var discApi1: Disc
    private lateinit var discApi2: Disc
    private lateinit var discApi3: Disc
    private lateinit var discApi4: Disc

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        val discApiFactory = DiscApiFactory()
        val databaseDiscFactory = DatabaseDiscFactory()
        discApi1 = discApiFactory.createDiscApi()
        discApi2 = discApiFactory.createDiscApi()
        discApi3 = discApiFactory.createDiscApi()
        discApi4 = discApiFactory.createDiscApi()
        listDiscApi = listOf(
            discApi1,
            discApi2,
            discApi3,
            discApi4
        )

        databaseDisc1 = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        databaseDisc2 = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        databaseDisc3 = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        listDatabaseDisc = listOf(
            databaseDisc1,
            databaseDisc2,
            databaseDisc3
        )

        discsRepository = DefaultDiscsRepository(
            discsLocalDataSource = FakeDataSource(
                discDatabaseItems = listDatabaseDisc.toMutableList(),
                discApiItems = listDiscApi
            ),
            discsPagingDataSource = FakeDataSource(
                discDatabaseItems = listDatabaseDisc.toMutableList(),
                discApiItems = listDiscApi
            ),
            ioDispatcher = Dispatchers.Main
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun countAllDiscs_FromDiscsLocalDataSource() = runTest {
        val count = discsRepository.countAllDiscs.first()

        assertEquals(listDatabaseDisc.size, count)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun countAllDiscs_FromDiscsLocalDataSource_Empty() = runTest {
        discsRepository = DefaultDiscsRepository(
            discsLocalDataSource = FakeDataSource(
                discApiItems = listDiscApi
            ),
            discsPagingDataSource = FakeDataSource(
                discApiItems = listDiscApi
            ),
            ioDispatcher = Dispatchers.Main
        )

        val count = discsRepository.countAllDiscs.first()

        assertTrue(count == 0)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun countFormatMediaList_FromDiscsLocalDataSource() = runTest {
        val list = discsRepository.countFormatMediaList.first()
        val size = listDatabaseDisc.groupBy { databaseDisc ->
            databaseDisc.formatMedia
        }.values.size

        assertEquals(size, list.size)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllDiscs_FromDiscsLocalDataSource() = runTest {
        // databaseDisc2 -> name_1
        val list = discsRepository.getAllDiscs(
            "searchQuery",
            SortOrder.BY_NAME
        ).first()

        val name = list.first().name

        assertEquals(databaseDisc2.name, name)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDiscWithId_FromDiscsLocalDataSource() {
        val disc = listDatabaseDisc.first().asDomainModel()
        val discRepo = discsRepository.getDiscWithId(disc.id).getOrAwaitValue()

        assertEquals(disc, discRepo)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertLong_FromDiscsLocalDataSource() = runTest {
        val addDisc = Disc(
            id = 4L,
            name = "name_4",
            title = "title_4",
            year = "year_4",
            country = "country_4",
            format = "format_4",
            formatMedia = "format_media_4",
            coverImage = "url_coverImage_4",
            barcode = "barcode_4",
            idDisc = 5,
            addBy = AddBy.SCAN
        )
        // Last added
        val index = discsRepository.insertLong(addDisc)

        assertEquals(4L, index)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun update_FromDiscsLocalDataSource() = runTest {
        val discUpdate = listDatabaseDisc.first().copy(
            name = "name_update",
            title = "title_update",
            year = "year_update"
        )

        discsRepository.update(discUpdate.asDomainModel())
        val list = discsRepository.getAllDiscs(
            "searchQuery",
            SortOrder.BY_NAME
        ).first()

        assertTrue(list.contains(discUpdate.asDomainModel()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteById_FromDiscsLocalDataSource() = runTest {
        discsRepository.deleteById(databaseDisc3.id)

        val list = discsRepository.getAllDiscs(
            "searchQuery",
            SortOrder.BY_NAME
        ).first()

        assertFalse(list.contains(databaseDisc3.asDomainModel()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getListDiscDbPresent_FromDiscsLocalDataSource() = runTest {
        val list = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = databaseDisc1.year
        )

        assertTrue(list.contains(databaseDisc1.asDomainModel()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getListDiscDbPresent_FromDiscsLocalDataSource_Empty() = runTest {
        val list = discsRepository.getListDiscDbPresent(
            name = "No_name_present_normalize",
            title = "No_title_present_normalize",
            year = "No_year_present"
        )

        assertTrue(list.isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeStream_FromDiscsPagingDataSource_NoPresent() = runTest {
        val pagingData = discsRepository.getSearchBarcodeStream(discApi4.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val listDisc = differ.snapshot().items

        assertTrue(listDisc.contains(discApi4))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeStream_FromDiscsPagingDataSource_PresentManually() = runTest {
        /* databaseDisc1 -> added manually and name_3
           discApi3 -> name_3 */
        val pagingData = discsRepository.getSearchBarcodeStream(discApi3.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertTrue(disc.isPresentByManually!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeStream_FromDiscsPagingDataSource_PresentScan() = runTest {
        /* databaseDisc2 -> added by scan and name_1
           discApi1 -> name_1 */
        val pagingData = discsRepository.getSearchBarcodeStream(discApi1.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertTrue(disc.isPresentByScan!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeStream_FromDiscsPagingDataSource_PresentSearch() = runTest {
        /* databaseDisc3 -> added by search and name_2
           discApi2 -> name_2 */
        val pagingData = discsRepository.getSearchBarcodeStream(discApi2.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertTrue(disc.isPresentBySearch!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchDiscStream_FromDiscsPagingDataSource() = runTest {
        // discApi4 -> name_4, title_4, year_4
        val discAdd = DiscAdd(
            name = "name_4",
            title = "title_4",
            year = "year_4",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_4",
            "title_normalize_4",
            discAdd.year
        )

        val discPresent = DiscPresent(listDb, discAdd)
        val pagingData = discsRepository.getSearchDiscStream(discPresent).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val listDisc = differ.snapshot().items

        assertTrue(listDisc.contains(discApi4))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchDiscStream_FromDiscsPagingDataSource_PresentManually() = runTest {
        /* disc in database added manually -> databaseDisc1
           disc will add -> name_3 (databaseDisc1.name) -> discApi3 */

        // From DiscPresentDetailFragment -> search button
        val discAdd = DiscAdd(
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = AddBy.MANUALLY
        )

        val listDb = discsRepository.getListDiscDbPresent(
            databaseDisc1.nameNormalize,
            databaseDisc1.titleNormalize,
            discAdd.year
        )

        val discPresent = DiscPresent(listDb, discAdd)
        val pagingData = discsRepository.getSearchDiscStream(discPresent).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertTrue(disc.isPresentByManually!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchDiscStream_FromDiscsPagingDataSource_PresentSearch() = runTest {
        /* disc in database added by search -> databaseDisc3
           disc will add -> name_2 (databaseDisc3.name) -> discApi2 */

        // From DiscPresentFragment -> search button
        val discAdd = DiscAdd(
            name = databaseDisc3.name,
            title = databaseDisc3.title,
            year = databaseDisc3.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            databaseDisc3.nameNormalize,
            databaseDisc3.titleNormalize,
            discAdd.year
        )

        val discPresent = DiscPresent(listDb, discAdd)
        val pagingData = discsRepository.getSearchDiscStream(discPresent).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertTrue(disc.isPresentBySearch!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeDatabase_FromDiscsPagingDataSource() = runTest {
        // databaseDisc2.barcode -> barcode_1
        val pagingData = discsRepository.getSearchBarcodeDatabase(databaseDisc2.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        assertEquals(databaseDisc2.asDomainModel(), disc)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getSearchBarcodeDatabase_FromDiscsPagingDataSource_PresentManuallySearch() = runTest {
        val databaseDisc4 = DatabaseDisc(
            id = 4L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_10",
            format = "format_10",
            formatMedia = "format_media_10",
            coverImage = "url_coverImage_10",
            barcode = "",
            idDisc = 5,
            addBy = AddBy.SEARCH,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        val databaseDisc5 = DatabaseDisc(
            id = 5L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = 6,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        listDatabaseDisc = listOf(
            databaseDisc1,
            databaseDisc2,
            databaseDisc3,
            databaseDisc4,
            databaseDisc5
        )

        discsRepository = DefaultDiscsRepository(
            discsLocalDataSource = FakeDataSource(
                discDatabaseItems = listDatabaseDisc.toMutableList(),
                discApiItems = listDiscApi
            ),
            discsPagingDataSource = FakeDataSource(
                discDatabaseItems = listDatabaseDisc.toMutableList(),
                discApiItems = listDiscApi
            ),
            ioDispatcher = Dispatchers.Main
        )

        // databaseDisc2.barcode -> barcode_1 name_1
        val pagingData = discsRepository.getSearchBarcodeDatabase(databaseDisc2.barcode).first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()

        val listDiscRepo = differ.snapshot().items
        val listDisc = listOf(databaseDisc2, databaseDisc4, databaseDisc5).asDomainModel()

        assertTrue(listDiscRepo.containsAll(listDisc))
    }
}