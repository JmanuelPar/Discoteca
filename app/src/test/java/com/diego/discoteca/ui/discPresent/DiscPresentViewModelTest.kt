package com.diego.discoteca.ui.discPresent

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.asDatabaseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscPresentViewModelTest {

    private lateinit var discPresentViewModel: DiscPresentViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>

    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        discsRepository = FakeDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        databaseDisc1 = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        databaseDisc2 = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        databaseDisc3 = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        listDatabaseDisc = listOf(
            databaseDisc1,
            databaseDisc2,
            databaseDisc3
        )

        discsRepository.setDatabaseDisc(listDatabaseDisc)
    }

    @Test
    fun test_OnButtonAddClicked_NavigateToDisc() = runTest {
        val discAdd = DiscAdd(
            name = "name_3",
            title = "title_3",
            year = "year_3",
            addBy = AddBy.MANUALLY
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_3",
            title = "title_normalize_3",
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentViewModel = DiscPresentViewModel(
            discsRepository,
            discPresent
        )

        discPresentViewModel.onButtonAddClicked()

        val result = discPresentViewModel.navigateToDisc.getOrAwaitValue()
        val uiText = result!!.first
        val id = result.second

        val listDatabaseDisc = discsRepository.getDatabaseDisc()
        val disc = Disc(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year,
            addBy = discAdd.addBy
        ).asDatabaseModel()
        val index = listDatabaseDisc.indexOf(disc) + 1

        assertEquals(UIText.DiscAdded, uiText)
        assertEquals(index.toLong(), id)
    }

    @Test
    fun test_OnButtonAddClicked_NavigateToDiscResultSearch() = runTest {
        val discAdd = DiscAdd(
            name = "name_2",
            title = "title_2",
            year = "year_2",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentViewModel = DiscPresentViewModel(
            discsRepository,
            discPresent
        )

        discPresentViewModel.onButtonAddClicked()
        val result = discPresentViewModel.navigateToDiscResultSearch.getOrAwaitValue()

        assertEquals(discPresent, result)
    }

    @Test
    fun test_OnButtonOkClicked_NavigateToDisc() = runTest {
        val databaseDisc4 = DatabaseDisc(
            id = 4L,
            name = "name_2",
            title = "title_2",
            year = "year_2",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_2",
            titleNormalize = "title_normalize_2"
        )

        discsRepository.setDatabaseDisc(listOf(databaseDisc4))

        val discAdd = DiscAdd(
            name = "name_2",
            title = "title_2",
            year = "year_2",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentViewModel = DiscPresentViewModel(
            discsRepository,
            discPresent
        )

        discPresentViewModel.onButtonOkClicked()
        val result = discPresentViewModel.navigateToDisc.getOrAwaitValue()

        val uiText = when (listDb.size) {
            1 -> UIText.DiscAlreadyPresentOne
            else -> UIText.DiscAlreadyPresentMore
        }

        val id = listDb[0].id

        assertEquals(uiText, result!!.first)
        assertEquals(id, result.second)
    }

    @Test
    fun test_OnButtonCancelClicked_NavigateToDisc() = runTest {
        val discAdd = DiscAdd(
            name = "name_2",
            title = "title_2",
            year = "year_2",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentViewModel = DiscPresentViewModel(
            discsRepository,
            discPresent
        )

        discPresentViewModel.onButtonCancelClicked()
        val result = discPresentViewModel.navigateToDisc.getOrAwaitValue()

        val uiText = when (listDb.size) {
            1 -> UIText.DiscAlreadyPresentOne
            else -> UIText.DiscAlreadyPresentMore
        }

        val id = listDb[0].id

        assertEquals(uiText, result!!.first)
        assertEquals(id, result.second)
    }
}