package com.diego.discoteca.ui.addDisc

import android.content.Context
import android.text.Editable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.database.asDatabaseModel
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DefaultDiscsRepository
import com.diego.discoteca.util.AddBy
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

//TODO : make test
@RunWith(AndroidJUnit4::class)
class  AddDiscViewModelTest {

   /* private lateinit var viewModel: AddDiscViewModel
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
        val repository = DefaultDiscsRepository(
            dao = db.discDatabaseDao,
            service = DiscogsApi.retrofitService,
            context = context
        )
        viewModel = AddDiscViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testNavigateToDisc() {
        viewModel.processingDisc(
            DiscAdd(
                name = "Disc title test",
                title = "Disc name test",
                year = "2002",
                addBy = AddBy.MANUALLY
            )
        )

        val result = viewModel.navigateToDisc.getOrAwaitValue()
        val id = 1L

        assertThat(result).isEqualTo(id)
    }

    @Test
    fun testNavigateToDiscPresent_listNotEmpty() = runBlocking {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = AddBy.MANUALLY
        )

        db.discDatabaseDao.insertLong(
            Disc(
                name = discAdd.name,
                title = discAdd.title,
                year = discAdd.year,
                addBy = discAdd.addBy
            ).asDatabaseModel()
        )

        viewModel.processingDisc(discAdd)
        val result = viewModel.navigateToDiscPresent.getOrAwaitValue()

        assertThat(result?.component1()?.isNotEmpty()).isTrue()
    }

    @Test
    fun testNavigateToDiscPresent_discAdd() = runBlocking {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = AddBy.MANUALLY
        )

        db.discDatabaseDao.insertLong(
            Disc(
                name = discAdd.name,
                title = discAdd.title,
                year = discAdd.year,
                addBy = discAdd.addBy
            ).asDatabaseModel()
        )

        viewModel.processingDisc(discAdd)
        val result = viewModel.navigateToDiscPresent.getOrAwaitValue()

        assertThat(result?.component2()).isEqualTo(discAdd)
    }

    @Test
    fun testNavigateToDiscResultSearch() {
        val discAdd = DiscAdd(
            name = "Disc title test",
            title = "Disc name test",
            year = "2002",
            addBy = AddBy.SEARCH
        )

        viewModel.processingDisc(discAdd)
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
                addBy = AddBy.MANUALLY
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
                addBy = AddBy.SEARCH
            )
        )
    }*/
}