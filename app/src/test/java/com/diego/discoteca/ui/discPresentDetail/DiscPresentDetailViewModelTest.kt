package com.diego.discoteca.ui.discPresentDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.asDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscPresentDetailViewModelTest {

    private lateinit var discPresentDetailViewModel: DiscPresentDetailViewModel
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
    fun test_GetDisc() = runTest {
        //DatabaseDisc1 clicked
        val discAdd = DiscAdd(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = databaseDisc1.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentDetailViewModel = DiscPresentDetailViewModel(
            discsRepository,
            discPresent
        )

        val result = discPresentDetailViewModel.getDisc().getOrAwaitValue()

        assertEquals(databaseDisc1.asDomainModel(), result)
    }

    @Test
    fun test_OnButtonSearchClicked_NavigateToDiscResultSearch() = runTest {
        // DatabaseDisc1 clicked
        /* Button search appears : disPresent.discAdd.addBy = AddBy.SEARCH and Disc in Db addBY = AddBy.MANUALLY
           addBy = AddBy.SEARCH */
        val discAdd = DiscAdd(
            id = databaseDisc3.id,
            name = databaseDisc3.name,
            title = databaseDisc3.title,
            year = databaseDisc3.year,
            addBy = databaseDisc3.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc3.nameNormalize,
            title = databaseDisc3.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentDetailViewModel = DiscPresentDetailViewModel(
            discsRepository,
            discPresent
        )

        discPresentDetailViewModel.onButtonSearchClicked()

        val result = discPresentDetailViewModel.navigateToDiscResultSearch.getOrAwaitValue()
        val addBy = result!!.discAdd.addBy

        assertEquals(discPresent, result)
        assertEquals(AddBy.MANUALLY, addBy)
    }

    @Test
    fun test_OnButtonCancelOkClicked_NavigatePopStack() = runTest {
        // DatabaseDisc1 clicked
        val discAdd = DiscAdd(
            id = databaseDisc2.id,
            name = databaseDisc2.name,
            title = databaseDisc2.title,
            year = databaseDisc2.year,
            addBy = databaseDisc2.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc2.nameNormalize,
            title = databaseDisc2.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        discPresentDetailViewModel = DiscPresentDetailViewModel(
            discsRepository,
            discPresent
        )

        discPresentDetailViewModel.onButtonCancelOkClicked()

        val result = discPresentDetailViewModel.navigatePopStack.getOrAwaitValue()

        assertEquals(true, result)
    }
}