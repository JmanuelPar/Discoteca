package com.diego.discoteca.ui.addDisc

import android.widget.EditText
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.MockEditable
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AddDiscViewModelTest {

    private lateinit var addDiscViewModel: AddDiscViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var artist: EditText
    private lateinit var title: EditText
    private lateinit var year: EditText
    private val id = 1L

    private val discAddManually = DiscAdd(
        name = "name_3",
        title = "title_3",
        year = "year_3",
        addBy = AddBy.MANUALLY
    )

    private val discAddSearch = DiscAdd(
        name = "name_2",
        title = "title_2",
        year = "year_2",
        addBy = AddBy.SEARCH
    )

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        discsRepository = FakeDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        listDatabaseDisc = listOf(
            databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY),
            databaseDiscFactory.createDatabaseDisc(AddBy.SCAN),
            databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        )

        addDiscViewModel = AddDiscViewModel(discsRepository)

        artist = mock(EditText::class.java)
        title = mock(EditText::class.java)
        year = mock(EditText::class.java)
    }

    @Test
    fun test_SetDiscArtist() {
        `when`(artist.text).thenReturn(MockEditable("Disc_artist"))

        addDiscViewModel.setDiscArtist(artist.text)
        val result = addDiscViewModel.errorMessageDiscArtist.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscArtist_Empty() {
        `when`(artist.text).thenReturn(MockEditable(""))

        addDiscViewModel.setDiscArtist(artist.text)
        val result = addDiscViewModel.errorMessageDiscArtist.getOrAwaitValue()

        assertEquals(UIText.DiscArtistNameIndicate, result)
    }

    @Test
    fun test_SetDiscTitle() {
        `when`(title.text).thenReturn(MockEditable("Disc_title"))

        addDiscViewModel.setDiscTitle(title.text)
        val result = addDiscViewModel.errorMessageDiscTitle.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscTitle_Empty() {
        `when`(title.text).thenReturn(MockEditable(""))

        addDiscViewModel.setDiscTitle(title.text)
        val result = addDiscViewModel.errorMessageDiscTitle.getOrAwaitValue()

        assertEquals(UIText.DiscTitleIndicate, result)
    }

    @Test
    fun test_SetDiscYear() {
        `when`(year.text).thenReturn(MockEditable("2022"))

        addDiscViewModel.setDiscYear(year.text)
        val result = addDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscYear_Empty() {
        `when`(year.text).thenReturn(MockEditable(""))

        addDiscViewModel.setDiscYear(year.text)
        val result = addDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(UIText.DiscYearIndicate, result)
    }

    @Test
    fun test_SetDiscYear_NoValidDiscYear() {
        `when`(year.text).thenReturn(MockEditable("1800"))

        addDiscViewModel.setDiscYear(year.text)
        val result = addDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(UIText.NoValidDiscYear, result)
    }

    @Test
    fun addDiscManually_NoPresent_NavigateToDisc() {
        //Disc add no present in database
        addDiscViewModel.processingDisc(discAddManually)

        val result = addDiscViewModel.navigateToDisc.getOrAwaitValue()

        assertEquals(id, result)
    }

    @Test
    fun addDiscManually_Present_NavigateToDiscPresent() = runTest {
        //Disc add present in database, manually -> name_3
        discsRepository.setDatabaseDisc(listDatabaseDisc)
        val listDiscDbPresent = discsRepository.getListDiscDbPresent(
            name = discAddManually.name,
            title = discAddManually.title,
            year = discAddManually.year
        )

        val discPresent = DiscPresent(listDiscDbPresent, discAddManually)

        addDiscViewModel.processingDisc(discAddManually)
        val result = addDiscViewModel.navigateToDiscPresent.getOrAwaitValue()

        assertEquals(discPresent, result)
    }

    @Test
    fun addDiscSearch_NoPresent_NavigateToDiscResultSearch() {
        //Disc add no present in database
        addDiscViewModel.processingDisc(discAddSearch)

        val listDiscDbPresent = emptyList<Disc>()
        val discPresent = DiscPresent(
            listDiscDbPresent,
            discAddSearch
        )

        val result = addDiscViewModel.navigateToDiscResultSearch.getOrAwaitValue()

        assertEquals(discPresent, result)
    }

    @Test
    fun addDiscSearch_Present_NavigateToDiscPresent() = runTest {
        //Disc add present in database, search -> name_1
        discsRepository.setDatabaseDisc(listDatabaseDisc)
        val listDiscDbPresent = discsRepository.getListDiscDbPresent(
            name = discAddSearch.name,
            title = discAddSearch.title,
            year = discAddSearch.year
        )

        val discPresent = DiscPresent(listDiscDbPresent, discAddSearch)

        addDiscViewModel.processingDisc(discAddSearch)
        val result = addDiscViewModel.navigateToDiscPresent.getOrAwaitValue()

        assertEquals(discPresent, result)
    }

    @Test
    fun test_OnButtonAddClicked() {
        `when`(artist.text).thenReturn(MockEditable("name_3"))
        `when`(title.text).thenReturn(MockEditable("title_3"))
        `when`(year.text).thenReturn(MockEditable("2022"))

        val discAdd = discAddManually.copy(year = "2022")

        addDiscViewModel.setDiscArtist(artist.text)
        addDiscViewModel.setDiscTitle(title.text)
        addDiscViewModel.setDiscYear(year.text)

        addDiscViewModel.onButtonAddClicked()
        val result = addDiscViewModel.showBottomSheet.getOrAwaitValue()

        assertEquals(discAdd, result)
    }

    @Test
    fun test_OnButtonSearchClicked() {
        `when`(artist.text).thenReturn(MockEditable("name_2"))
        `when`(title.text).thenReturn(MockEditable("title_2"))
        `when`(year.text).thenReturn(MockEditable("2022"))

        val discAdd = discAddSearch.copy(year = "2022")

        addDiscViewModel.setDiscArtist(artist.text)
        addDiscViewModel.setDiscTitle(title.text)
        addDiscViewModel.setDiscYear(year.text)

        addDiscViewModel.onButtonSearchClicked()
        val result = addDiscViewModel.showBottomSheet.getOrAwaitValue()

        assertEquals(discAdd, result)
    }
}