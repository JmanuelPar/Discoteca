package com.diego.discoteca.ui.updateDisc

import android.widget.EditText
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.*
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.asDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class UpdateDiscViewModelTest {

    private lateinit var updateDiscViewModel: UpdateDiscViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>

    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc
    private lateinit var artist: EditText
    private lateinit var title: EditText
    private lateinit var year: EditText

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

        updateDiscViewModel = UpdateDiscViewModel(
            discsRepository,
            databaseDisc1.id
        )

        artist = mock(EditText::class.java)
        title = mock(EditText::class.java)
        year = mock(EditText::class.java)
    }

    @Test
    fun test_GetDisc() {
        val result = updateDiscViewModel.getDisc().getOrAwaitValue()

        assertEquals(databaseDisc1.asDomainModel(), result)
    }

    @Test
    fun test_SetDiscArtist() {
        `when`(artist.text).thenReturn(MockEditable("Disc_artist"))

        updateDiscViewModel.setDiscArtist(artist.text)
        val result = updateDiscViewModel.errorMessageDiscArtist.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscArtist_Empty() {
        `when`(artist.text).thenReturn(MockEditable(""))

        updateDiscViewModel.setDiscArtist(artist.text)
        val result = updateDiscViewModel.errorMessageDiscArtist.getOrAwaitValue()

        assertEquals(UIText.DiscArtistNameIndicate, result)
    }

    @Test
    fun test_SetDiscTitle() {
        `when`(title.text).thenReturn(MockEditable("Disc_title"))

        updateDiscViewModel.setDiscTitle(title.text)
        val result = updateDiscViewModel.errorMessageDiscTitle.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscTitle_Empty() {
        `when`(title.text).thenReturn(MockEditable(""))

        updateDiscViewModel.setDiscTitle(title.text)
        val result = updateDiscViewModel.errorMessageDiscTitle.getOrAwaitValue()

        assertEquals(UIText.DiscTitleIndicate, result)
    }

    @Test
    fun test_SetDiscYear() {
        `when`(year.text).thenReturn(MockEditable("2022"))

        updateDiscViewModel.setDiscYear(year.text)
        val result = updateDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(null, result)
    }

    @Test
    fun test_SetDiscYear_Empty() {
        `when`(year.text).thenReturn(MockEditable(""))

        updateDiscViewModel.setDiscYear(year.text)
        val result = updateDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(UIText.DiscYearIndicate, result)
    }

    @Test
    fun test_SetDiscYear_NoValidDiscYear() {
        `when`(year.text).thenReturn(MockEditable("1800"))

        updateDiscViewModel.setDiscYear(year.text)
        val result = updateDiscViewModel.errorMessageDiscYear.getOrAwaitValue()

        assertEquals(UIText.NoValidDiscYear, result)
    }

    @Test
    fun test_UpdateButtonClicked() {
        `when`(artist.text).thenReturn(MockEditable("name_update"))
        `when`(title.text).thenReturn(MockEditable("title_update"))
        `when`(year.text).thenReturn(MockEditable("year_update"))

        updateDiscViewModel.setDiscArtist(artist.text)
        updateDiscViewModel.setDiscTitle(title.text)
        updateDiscViewModel.setDiscYear(year.text)

        val discUpdate = databaseDisc1.asDomainModel()
            .copy(
                name = "name_update",
                title = "title_update",
                year = "year_update"
            )

        updateDiscViewModel.updateButtonClicked()
        val result = updateDiscViewModel.showBottomSheet.getOrAwaitValue()

        assertEquals(discUpdate, result)
    }

    @Test
    fun test_UpdateDisc() {
        val discUpdate = databaseDisc1.asDomainModel()
            .copy(
                name = "name_update",
                title = "title_update",
                year = "year_update"
            )

        updateDiscViewModel.updateDisc(discUpdate)
        val result = updateDiscViewModel.navigateToDisc.getOrAwaitValue()

        assertEquals(discUpdate.id, result)
    }
}