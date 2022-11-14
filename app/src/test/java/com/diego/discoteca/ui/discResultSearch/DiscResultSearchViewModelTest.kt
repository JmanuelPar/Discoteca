package com.diego.discoteca.ui.discResultSearch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import com.diego.discoteca.*
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscResultSearchViewModelTest {

    private lateinit var discResultSearchViewModel: DiscResultSearchViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var listDiscApi: List<Disc>

    private val databaseDisc1 = DatabaseDisc(
        id = 1L,
        name = "name_1",
        title = "title_1",
        year = "year_1",
        country = "country_1",
        format = "format_1",
        formatMedia = "format_media_1",
        coverImage = "url_coverImage_1",
        barcode = "",
        idDisc = 2,
        addBy = AddBy.SEARCH,
        nameNormalize = "name_normalize_1",
        titleNormalize = "title_normalize_1"
    )

    private lateinit var discApi1: Disc
    private lateinit var discApi2: Disc
    private lateinit var discApi3: Disc

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        discsRepository = FakeDiscsRepository()
        val discApiFactory = DiscApiFactory()

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
    fun test_Init() = runTest {
        val discAdd = DiscAdd(
            name = "name_3",
            title = "title_3",
            year = "year_3",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discResultSearchViewModel = DiscResultSearchViewModel(
            discsRepository,
            discPresent
        )

        val result = discResultSearchViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listDisc = differ.snapshot().items

        assertEquals(
            true,
            listDisc.contains(discApi3)
        )
    }

    @Test
    fun test_UpdateNbPresentDisc() = runTest {
        val discAdd = DiscAdd(
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discResultSearchViewModel = DiscResultSearchViewModel(
            discsRepository,
            discPresent
        )

        val result = discResultSearchViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listSnapshot = differ.snapshot()
        discResultSearchViewModel.updateNbPresentDisc(listSnapshot)

        val resultManually = discResultSearchViewModel.nBManually.getOrAwaitValue()
        val resultScan = discResultSearchViewModel.nBScan.getOrAwaitValue()
        val resultSearch = discResultSearchViewModel.nBSearch.getOrAwaitValue()

        assertEquals(0, resultManually)
        assertEquals(0, resultScan)
        assertEquals(1, resultSearch)
    }

    @Test
    fun test_UpdateTotal() = runTest {
        val discAdd = DiscAdd(
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discResultSearchViewModel = DiscResultSearchViewModel(
            discsRepository,
            discPresent
        )

        val result = discResultSearchViewModel.pagingDataFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiscDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        advanceUntilIdle()

        val listSize = differ.snapshot().items.size
        discResultSearchViewModel.updateTotalResult(listSize)

        val resultTotal = discResultSearchViewModel.totalResult.getOrAwaitValue()

        assertEquals(listSize, resultTotal)
    }
}