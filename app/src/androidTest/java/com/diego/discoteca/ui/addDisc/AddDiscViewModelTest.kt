package com.diego.discoteca.ui.addDisc

import android.content.Context
import android.text.Editable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.R
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.database.getOrAwaitValue
import com.diego.discoteca.model.DiscAdd
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.MANUALLY
import com.diego.discoteca.util.SEARCH
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AddDiscViewModelTest {

    private lateinit var viewModel: AddDiscViewModel
    private lateinit var db: DiscRoomDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DiscRoomDatabase::class.java
        ).allowMainThreadQueries().build()
        val repository = DiscRepository(db.discDatabaseDao)
        viewModel = AddDiscViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testNavigateToDisc() {
        viewModel.addDisc(
            DiscAdd(
                name = "Disc title test",
                title = "Disc name test",
                year = "2002",
                addBy = MANUALLY
            )
        )

        val result = viewModel.navigateToDisc.getOrAwaitValue()
        val snackBarMessage =
            ApplicationProvider.getApplicationContext<Context>().getString(R.string.disc_added)
        val id = 1L

        assertThat(result).isEqualTo(Pair(snackBarMessage, id))
    }

    @Test
    fun testNavigateToDiscPresent_listNotEmpty() {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = MANUALLY
        )

        viewModel.addDisc(discAdd)
        viewModel.addDisc(discAdd)
        val result = viewModel.navigateToDiscPresent.getOrAwaitValue()

        assertThat(result?.component1()?.isNotEmpty()).isTrue()
    }

    @Test
    fun testNavigateToDiscPresent_discAdd() {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = MANUALLY
        )

        viewModel.addDisc(discAdd)
        viewModel.addDisc(discAdd)
        val result = viewModel.navigateToDiscPresent.getOrAwaitValue()

        assertThat(result?.component2()).isEqualTo(discAdd)
    }

    @Test
    fun testNavigateToDiscResultSearch() {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = SEARCH
        )

        viewModel.searchDisc(discAdd)
        val result = viewModel.navigateToDiscResultSearch.getOrAwaitValue()
        val list = result?.component1()?.isEmpty()
        val disc = result?.component2()?.equals(discAdd)

        assertThat(list == true && disc == true).isTrue()
    }

    @Test
    fun testOnButtonAddClicked() {
        val title = Editable.Factory.getInstance().newEditable("Disc name test")
        val name = Editable.Factory.getInstance().newEditable("Disc title test")
        val year = Editable.Factory.getInstance().newEditable("Disc year test")

        viewModel.setDiscArtist(title)
        viewModel.setDiscTitle(name)
        viewModel.setDiscYear(year)
        viewModel.onButtonAddClicked()

        val result = viewModel.showBottomSheet.getOrAwaitValue()

        assertThat(result).isEqualTo(
            DiscAdd(
                name = title.toString(),
                title = name.toString(),
                year = year.toString(),
                addBy = MANUALLY
            )
        )
    }

    @Test
    fun testOnButtonSearchClicked() {
        val title = Editable.Factory.getInstance().newEditable("Disc name test")
        val name = Editable.Factory.getInstance().newEditable("Disc title test")
        val year = Editable.Factory.getInstance().newEditable("Disc year test")

        viewModel.setDiscArtist(title)
        viewModel.setDiscTitle(name)
        viewModel.setDiscYear(year)
        viewModel.onButtonSearchClicked()

        val result = viewModel.showBottomSheet.getOrAwaitValue()

        assertThat(result).isEqualTo(
            DiscAdd(
                name = title.toString(),
                title = name.toString(),
                year = year.toString(),
                addBy = SEARCH
            )
        )
    }
}