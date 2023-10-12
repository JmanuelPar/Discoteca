package com.diego.discoteca.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.Constants.DATABASE_PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class DiscRoomDatabaseTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: DiscRoomDatabase
    private lateinit var discDao: DiscDatabaseDao
    private lateinit var discDatabase1: DatabaseDisc
    private lateinit var discDatabase2: DatabaseDisc
    private lateinit var discDatabase3: DatabaseDisc

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DiscRoomDatabase::class.java
        ).allowMainThreadQueries().build()
        discDao = db.discDatabaseDao()

        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabase1 = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabase2 = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabase3 = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertDisc() = runTest {
        discDao.insertLong(discDatabase1)
        val databaseDisc = discDao.getDiscWithId(discDatabase1.id).getOrAwaitValue()

        assertEquals(discDatabase1, databaseDisc)
    }

    @Test
    fun deleteDisc() = runTest {
        discDao.insertLong(discDatabase1)
        discDao.deleteById(discDatabase1.id)

        val allDiscs = discDao.getAllDiscsByName(discDatabase1.name).first()

        assertTrue(allDiscs.isEmpty())
    }

    @Test
    fun updateDiscByName() = runTest {
        discDao.insertLong(discDatabase1)
        val discUpdate = discDatabase1.copy(name = "name_update")

        discDao.update(discUpdate)
        val allDiscs = discDao.getAllDiscsByName(discUpdate.name).first()

        assertTrue(allDiscs.contains(discUpdate))
    }

    @Test
    fun updateDiscByTitle() = runTest {
        discDao.insertLong(discDatabase1)
        val discUpdate = discDatabase1.copy(title = "title_update")

        discDao.update(discUpdate)
        val allDiscs = discDao.getAllDiscsByName(discUpdate.title).first()

        assertTrue(allDiscs.contains(discUpdate))
    }

    @Test
    fun updateDiscByYear() = runTest {
        discDao.insertLong(discDatabase1)
        val discUpdate = discDatabase1.copy(year = "year_update")

        discDao.update(discUpdate)
        val allDiscs = discDao.getAllDiscsByName(discUpdate.year).first()

        assertTrue(allDiscs.contains(discUpdate))
    }

    @Test
    fun countAllDiscs() = runTest {
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase3)
        }

        val countDiscs = discDao.countAllDiscs().first()

        assertEquals(3, countDiscs)
    }

    @Test
    fun countFormatMedia() = runTest {
        val discFormatCD = discDatabase2.copy(formatMedia = "CD")
        val discFormatVinyl = discDatabase3.copy(formatMedia = "Vinyl")

        // 1 "Nothing" + 1 CD + 1 Vinyl
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discFormatCD)
            insertLong(discFormatVinyl)
        }

        val list = discDao.getCountFormatMediaList().first()
        val totalFormatMedia = list.count { it.countMedia.toInt() > 0 }
        val countMediaList = list.map { it.countMedia }.size

        assertEquals(3, totalFormatMedia)
        assertEquals(list.size, countMediaList)
    }

    @Test
    fun getAllDiscsByName() = runTest {
        val discDatabase4 = DatabaseDisc(
            id = 4L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        // discDatabase2 -> name_1
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase4)
        }

        val list = listOf(discDatabase2, discDatabase4)
        val allDiscs = discDao.getAllDiscsByName(discDatabase2.name).first()

        assertTrue(allDiscs.containsAll(list))
    }

    @Test
    fun getAllDiscsByTitle() = runTest {
        val discDatabase4 = DatabaseDisc(
            id = 4L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        // discDatabase2 -> title_1
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase4)
        }

        val list = listOf(discDatabase2, discDatabase4)
        val allDiscs = discDao.getAllDiscsByTitle(discDatabase2.title).first()

        assertTrue(allDiscs.containsAll(list))
    }

    @Test
    fun getAllDiscsByYear() = runTest {
        val discDatabase4 = DatabaseDisc(
            id = 4L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        // discDatabase2 -> year_1
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase4)
        }

        val list = listOf(discDatabase2, discDatabase4)
        val allDiscs = discDao.getAllDiscsByYear(discDatabase2.year).first()

        assertTrue(allDiscs.containsAll(list))
    }

    @Test
    fun getListDiscDbScan() = runTest {
        val discDatabase4 = DatabaseDisc(
            id = 4L,
            name = "name_4",
            title = "title_4",
            year = "year_4",
            country = "country_4",
            format = "format_4",
            formatMedia = "format_media_4",
            coverImage = "url_coverImage_4",
            barcode = "barcode_1",
            idDisc = 5,
            addBy = AddBy.SCAN,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase3)
            insertLong(discDatabase4)
        }

        val list = listOf(discDatabase2, discDatabase4)
        val listDb = discDao.getListDiscDbScan(discDatabase2.barcode)

        assertTrue(listDb.containsAll(list))
    }

    @Test
    fun getDiscDbManually() = runTest {
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase3)
        }

        val databaseDisc = discDao.getDiscDbManually(
            discDatabase1.nameNormalize,
            discDatabase1.titleNormalize,
            discDatabase1.year,
            AddBy.MANUALLY.code
        )

        assertEquals(discDatabase1, databaseDisc)
    }

    @Test
    fun getDiscDbSearch() = runTest {
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase3)
        }

        val databaseDisc = discDao.getDiscDbSearch(
            discDatabase3.idDisc,
            discDatabase3.name,
            discDatabase3.title,
            discDatabase3.year,
            AddBy.SEARCH.code
        )

        assertEquals(discDatabase3, databaseDisc)
    }

    @Test
    fun getListDiscDbPresent() = runTest {
        discDao.apply {
            insertLong(discDatabase1)
            insertLong(discDatabase2)
            insertLong(discDatabase3)
        }

        val databaseDisc = discDao.getListDiscDbPresent(
            discDatabase1.nameNormalize,
            discDatabase1.titleNormalize,
            discDatabase1.year
        ).first()

        assertEquals(discDatabase1, databaseDisc)
    }

    @Test
    fun getListDiscDbManuallySearch() = runTest {
        val discDatabaseS = DatabaseDisc(
            id = 1L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_10",
            format = "format_10",
            formatMedia = "format_media_10",
            coverImage = "url_coverImage_10",
            barcode = "",
            idDisc = 2,
            addBy = AddBy.SEARCH,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        val discDatabaseM = DatabaseDisc(
            id = 3L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = 4,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        discDao.apply {
            insertLong(discDatabaseS)
            insertLong(discDatabase2)
            insertLong(discDatabaseM)
        }

        val list = listOf(discDatabaseS, discDatabaseM)
        val listDao = discDao.getListDiscDbManuallySearch(
            name = discDatabase2.nameNormalize,
            title = discDatabase2.titleNormalize,
            year = discDatabase2.year,
            addByManual = AddBy.MANUALLY.code,
            addBySearch = AddBy.SEARCH.code
        )

        assertEquals(list, listDao)
    }

    @Test
    fun getPagedListBarcode() = runTest {
        discDao.insertLong(discDatabase2)

        val databaseDisc = discDao.getPagedListBarcode(
            discDatabase2.barcode,
            3 * DATABASE_PAGE_SIZE,
            0
        ).first()

        assertEquals(discDatabase2, databaseDisc)
    }
}