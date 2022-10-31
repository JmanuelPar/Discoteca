package com.diego.discoteca.ui.discResultDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.asDatabaseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscResultDetailViewModelTest {

    private lateinit var discResultDetailViewModel: DiscResultDetailViewModel
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
    fun test_OnButtonYesClicked_Search_OnNavigateToDisc() {
        /* From discResultSearchFragment
           Disc from Api */
        val discChosen = Disc(
            name = "name_4",
            title = "title_4",
            year = "year_4",
            country = "country_4",
            format = "format_4",
            formatMedia = "formatMedia_4",
            coverImage = "url_coverImage_4",
            barcode = "barcode_4",
            idDisc = 5,
            addBy = AddBy.NONE
        )

        val discResultDetail = DiscResultDetail(
            disc = discChosen,
            addBy = AddBy.SEARCH
        )

        discResultDetailViewModel = DiscResultDetailViewModel(
            discsRepository,
            discResultDetail
        )

        /* discChosen.isPresentByManually = false
           discChosen.isPresentByScan = false
           discChosen.isPresentBySearch = false
           We add this disc */
        discResultDetailViewModel.onButtonYesClicked()

        val result = discResultDetailViewModel.navigateToDisc.getOrAwaitValue()
        val uiText = result!!.first
        val id = result.second

        val listDatabaseDisc = discsRepository.getDatabaseDisc()
        val index = listDatabaseDisc.indexOf(discChosen.asDatabaseModel()) + 1

        assertEquals(UIText.DiscAdded, uiText)
        assertEquals(index.toLong(), id)
    }

    @Test
    fun test_OnButtonYesClicked_Scan_NavigatePopStack() {
        /* From discResultScanFragment
           Disc from Api present already in database, added by Scan */
        val discChosen = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "barcode_1",
            idDisc = 2,
            addBy = AddBy.NONE
        )
        discChosen.isPresentByScan = true

        val discResultDetail = DiscResultDetail(
            disc = discChosen,
            addBy = AddBy.SCAN
        )

        discResultDetailViewModel = DiscResultDetailViewModel(
            discsRepository,
            discResultDetail
        )

        /* discChosen.isPresentByManually = false
           discChosen.isPresentByScan = true
           discChosen.isPresentBySearch = false
           We PopStack */
        discResultDetailViewModel.onButtonYesClicked()

        val result = discResultDetailViewModel.navigatePopStack.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_OnButtonYesClicked_Scan_NavigateToDisc() {
        /* From discResultScanFragment
           Disc from Api present already in database, added by Manually */
        val discChosen = Disc(
            name = "name_3",
            title = "title_3",
            year = "year_3",
            country = "country_3",
            format = "format_3",
            formatMedia = "formatMedia_3",
            coverImage = "url_coverImage_3",
            barcode = "barcode_3",
            idDisc = 30,
            addBy = AddBy.NONE
        )
        discChosen.isPresentByManually = true
        discChosen.discLight = DiscLight(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            country = "",
            format = "",
            formatMedia = ""
        )

        val discResultDetail = DiscResultDetail(
            disc = discChosen,
            addBy = AddBy.SCAN
        )

        discResultDetailViewModel = DiscResultDetailViewModel(
            discsRepository,
            discResultDetail
        )

        /* discChosen.isPresentByManually = true
           discChosen.isPresentByScan = false
           discChosen.isPresentBySearch = false
           We update this disc */
        discResultDetailViewModel.onButtonYesClicked()

        val result = discResultDetailViewModel.navigateToDisc.getOrAwaitValue()
        val uiText = result!!.first
        val id = result.second

        assertEquals(UIText.DiscUpdated, uiText)
        assertEquals(discChosen.discLight!!.id, id)
    }

    @Test
    fun test_OnButtonNoClicked_NavigatePopStack() {
        /* From discResultSearchFragment
           Disc from Api */
        val discChosen = Disc(
            name = "name_4",
            title = "title_4",
            year = "year_4",
            country = "country_4",
            format = "format_4",
            formatMedia = "formatMedia_4",
            coverImage = "url_coverImage_4",
            barcode = "barcode_4",
            idDisc = 5,
            addBy = AddBy.NONE
        )

        val discResultDetail = DiscResultDetail(
            disc = discChosen,
            addBy = AddBy.SEARCH
        )

        discResultDetailViewModel = DiscResultDetailViewModel(
            discsRepository,
            discResultDetail
        )

        discResultDetailViewModel.onButtonNoClicked()

        val result = discResultDetailViewModel.navigatePopStack.getOrAwaitValue()

        assertEquals(true, result)
    }
}