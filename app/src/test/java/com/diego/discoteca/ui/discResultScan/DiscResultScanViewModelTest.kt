package com.diego.discoteca.ui.discResultScan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import com.diego.discoteca.*
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscResultScan
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.Destination
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.asDomainModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscResultScanViewModelTest {

    private lateinit var discResultScanViewModel: DiscResultScanViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var listDiscApi: List<Disc>

    private val barcode = "barcode_1"
    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var discApi1: Disc
    private lateinit var discApi2: Disc
    private lateinit var discApi3: Disc

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        discsRepository = FakeDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        val discApiFactory = DiscApiFactory()

        databaseDisc1 = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        listDatabaseDisc = listOf(databaseDisc1)
        discsRepository.setDatabaseDisc(listDatabaseDisc)

        discApi1 = discApiFactory.createDiscApi()
        discApi2 = discApiFactory.createDiscApi()
        discApi3 = discApiFactory.createDiscApi()
        listDiscApi = listOf(
            discApi1,
            discApi2,
            discApi3
        )

        discsRepository.setDiscApi(listDiscApi)
    }

    @Test
    fun test_To_Api_Init() = runTest {
        val discResultScan = DiscResultScan(
            barcode = barcode,
            destination = Destination.API
        )

        discResultScanViewModel = DiscResultScanViewModel(
            discsRepository,
            discResultScan
        )

        val result = discResultScanViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val disc = differ.snapshot().items.first()

        // barcode_1 : present in Api (discApi1) and in database (databaseDisc1 added by scan)
        assertEquals(discApi1, disc)
        assertTrue(disc.isPresentByScan!!)
    }

    @Test
    fun test_To_Database_Init() = runTest {
        val databaseDisc2 = DatabaseDisc(
            id = 2L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "format_media_1",
            coverImage = "url_coverImage_1",
            barcode = "",
            idDisc = 3,
            addBy = AddBy.SEARCH,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        val databaseDisc3 = DatabaseDisc(
            id = 3L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = 4,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        val listManuallySearch = listOf(databaseDisc2, databaseDisc3)
        discsRepository.setDatabaseDisc(listManuallySearch)

        val discResultScan = DiscResultScan(
            barcode = barcode,
            destination = Destination.DATABASE
        )

        discResultScanViewModel = DiscResultScanViewModel(
            discsRepository,
            discResultScan
        )

        val result = discResultScanViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listDiscRepo = differ.snapshot().items
        val listDisc = listManuallySearch.asDomainModel()

        assertTrue(listDiscRepo.containsAll(listDisc))
    }

    @Test
    fun test_To_Api_UpdateNbDisc() = runTest {
        val discResultScan = DiscResultScan(
            barcode = barcode,
            destination = Destination.API
        )

        discResultScanViewModel = DiscResultScanViewModel(
            discsRepository,
            discResultScan
        )

        val result = discResultScanViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listSnapshot = differ.snapshot()
        discResultScanViewModel.updateNbDisc(listSnapshot)

        val resultManually = discResultScanViewModel.nBManually.getOrAwaitValue()
        val resultScan = discResultScanViewModel.nBScan.getOrAwaitValue()
        val resultSearch = discResultScanViewModel.nBSearch.getOrAwaitValue()

        assertEquals(0, resultManually)
        assertEquals(1, resultScan)
        assertEquals(0, resultSearch)
    }

    @Test
    fun test_To_Database_TotalResult() = runTest {
        val discResultScan = DiscResultScan(
            barcode = barcode,
            destination = Destination.DATABASE
        )

        discResultScanViewModel = DiscResultScanViewModel(
            discsRepository,
            discResultScan
        )

        val result = discResultScanViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listSize = differ.snapshot().items.size

        discResultScanViewModel.updateTotal(listSize)
        val resultTotal = discResultScanViewModel.totalResult.getOrAwaitValue()

        assertEquals(UIText.TotalDatabase(listSize), resultTotal)
    }

    @Test
    fun test_OnButtonBackClicked() {
        val discResultScan = DiscResultScan(
            barcode = barcode,
            destination = Destination.API
        )

        discResultScanViewModel = DiscResultScanViewModel(
            discsRepository,
            discResultScan
        )

        discResultScanViewModel.onButtonBackClicked()
        val result = discResultScanViewModel.buttonBack.getOrAwaitValue()

        assertEquals(discResultScan.destination, result)
    }
}