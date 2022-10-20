package com.diego.discoteca.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DiscRoomDatabaseTest : TestCase() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var discDao: DiscDatabaseDao
    private lateinit var db: DiscRoomDatabase

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DiscRoomDatabase::class.java
        ).allowMainThreadQueries().build()
        discDao = db.discDatabaseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertDisc() = runBlocking {
        val discDatabase = Disc(
            name = "Disc name test",
            title = "Disc title test",
            year = "2022",
            addBy = AddBy.MANUALLY
        ).asDatabaseModel()

        val idDatabaseDisc = discDao.insertLong(discDatabase)
        val databaseDisc = discDao.getDiscWithId(idDatabaseDisc).getOrAwaitValue()
        assertEquals(databaseDisc.id, idDatabaseDisc)
    }

    @Test
    @Throws(Exception::class)
    fun deleteDisc() = runBlocking {
        val discDatabase = Disc(
            name = "Disc name test",
            title = "Disc title test",
            year = "2022",
            addBy = AddBy.MANUALLY
        ).asDatabaseModel()

        val idDatabaseDisc = discDao.insertLong(discDatabase)
        discDao.deleteById(idDatabaseDisc)
        val listDiscs = discDao.getAllDiscsByName("Disc name test").first()
        assertThat(listDiscs).doesNotContain(discDatabase)
    }

    @Test
    @Throws(Exception::class)
    fun updateDiscByName() = runBlocking {
        val disc = Disc(
            name = "Disc name test",
            title = "Disc title test",
            year = "2022",
            addBy = AddBy.MANUALLY
        )

        val idDiscDatabase = discDao.insertLong(disc.asDatabaseModel())
        val discUpdate = Disc(
            id = idDiscDatabase,
            name = "Disc name update",
            title = disc.title,
            year = disc.year,
            country = disc.country,
            format = disc.format,
            formatMedia = disc.formatMedia,
            coverImage = disc.coverImage,
            barcode = disc.barcode,
            idDisc = disc.idDisc,
            addBy = disc.addBy,
            discLight = disc.discLight
        )

        discDao.update(discUpdate.asDatabaseModel())
        val listDiscs = discDao.getAllDiscsByName("Disc name update").first()
        assertThat(listDiscs).contains(discUpdate.asDatabaseModel())
    }

    @Test
    @Throws(Exception::class)
    fun updateDiscByTitle() = runBlocking {
        val disc = Disc(
            name = "Disc name test",
            title = "Disc title test",
            year = "2022",
            addBy = AddBy.MANUALLY
        )

        val idDiscDatabase = discDao.insertLong(disc.asDatabaseModel())
        val discUpdate = Disc(
            id = idDiscDatabase,
            name = disc.name,
            title = "Disc title update",
            year = disc.year,
            country = disc.country,
            format = disc.format,
            formatMedia = disc.formatMedia,
            coverImage = disc.coverImage,
            barcode = disc.barcode,
            idDisc = disc.idDisc,
            addBy = disc.addBy,
            discLight = disc.discLight
        )

        discDao.update(discUpdate.asDatabaseModel())
        val listDiscs = discDao.getAllDiscsByName("Disc title update").first()
        assertThat(listDiscs).contains(discUpdate.asDatabaseModel())
    }

    @Test
    @Throws(Exception::class)
    fun countAllDiscs() = runBlocking {
        val discDatabaseOne = Disc(
            name = "Disc one name test",
            title = "Disc one title test",
            year = "2022",
            addBy = AddBy.MANUALLY
        ).asDatabaseModel()

        val discDatabaseTwo = Disc(
            name = "Disc two name test",
            title = "Disc two title test",
            year = "2018",
            addBy = AddBy.MANUALLY
        ).asDatabaseModel()

        discDao.insertLong(discDatabaseOne)
        discDao.insertLong(discDatabaseTwo)
        val countDiscs = discDao.countAllDiscs().first()
        assertEquals(2, countDiscs)
    }

    @Test
    @Throws(Exception::class)
    fun countFormatMedia() = runBlocking {
        // FormatMedia = ""
        val discDatabaseOne = Disc(
            name = "Booba",
            title = "Temps Mort",
            year = "2002",
            addBy = AddBy.MANUALLY
        )

        // FormatMedia = ""
        val discDatabaseTwo = Disc(
            name = "Lunatic",
            title = "Mauvais Oeil",
            year = "2000",
            addBy = AddBy.MANUALLY
        )

        // FormatMedia = "CD"
        val discDatabaseThree = Disc(
            name = "Daddy Lord C & La Cliqua",
            title = "Freaky Flow Remix",
            year = "1995",
            country = "France",
            format = "1 Media CD : Maxi-Single",
            formatMedia = "CD",
            coverImage = "",
            barcode = "3448963601129",
            idDisc = 915570,
            addBy = AddBy.SCAN
        )

        discDao.insertLong(discDatabaseOne.asDatabaseModel())
        discDao.insertLong(discDatabaseTwo.asDatabaseModel())
        discDao.insertLong(discDatabaseThree.asDatabaseModel())

        val list = discDao.getCountFormatMediaList().first()

        var totalFormatMedia = 0
        list.forEach { totalFormatMedia += it.countMedia.toInt() }
        val countMediaList = list.map { it.countMedia }.size

        assertEquals(3, totalFormatMedia)
        assertEquals(2, countMediaList)
    }
}