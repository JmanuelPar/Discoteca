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

@OptIn(ExperimentalCoroutinesApi::class)
class DiscPresentDetailViewModelTest {

    private lateinit var discPresentDetailViewModel: DiscPresentDetailViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>

    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc

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
        /* We want to add a disc manually
           DatabaseDisc1 (added manually) was clicked -> name_3
           Disc want to add is present in Db */
        val discAdd = DiscAdd(
            name = "name_3",
            title = "title_3",
            year = "year_3",
            addBy = AddBy.MANUALLY
        )
        val discAddForDetail = discAdd.copy(id = databaseDisc1.id)

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddForDetail
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
        /* We want to add a disc by search
           DatabaseDisc1 (added manually) was clicked -> name_3
           Disc want to add is present in Db */

        /* Button search appears : disPresent.discAdd.addBy = AddBy.SEARCH and disc in Db addBY = AddBy.MANUALLY
           addBy = AddBy.SEARCH */
        val discAdd = DiscAdd(
            name = "name_3",
            title = "title_3",
            year = "year_3",
            addBy = AddBy.SEARCH
        )
        val discAddForDetail = discAdd.copy(id = databaseDisc1.id)

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddForDetail
        )

        discPresentDetailViewModel = DiscPresentDetailViewModel(
            discsRepository,
            discPresent
        )

        discPresentDetailViewModel.onButtonSearchClicked()

        val result = discPresentDetailViewModel.navigateToDiscResultSearch.getOrAwaitValue()
        val listDbResult = result!!.list
        val addByResult = result.discAdd.addBy

        assertEquals(listDb, listDbResult)
        assertEquals(AddBy.MANUALLY, addByResult)
    }

    @Test
    fun test_OnButtonCancelOkClicked_NavigatePopStack() = runTest {
        /* We want to add a disc by search
           DatabaseDisc2 (added by scan) was clicked -> name_1
           Disc want to add is present in Db
           Button Ok appears */
        val discAdd = DiscAdd(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            addBy = AddBy.SEARCH
        )
        val discAddForDetail = discAdd.copy(id = databaseDisc2.id)

        val listDb = discsRepository.getListDiscDbPresent(
            name = discAdd.name,
            title = discAdd.title,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddForDetail
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