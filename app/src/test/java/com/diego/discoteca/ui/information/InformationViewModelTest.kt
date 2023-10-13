package com.diego.discoteca.ui.information

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InformationViewModelTest {

    private lateinit var informationViewModel: InformationViewModel
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
        informationViewModel = InformationViewModel(discsRepository)
    }

    @Test
    fun test_CountDiscs() {
        val result = informationViewModel.countDiscs.getOrAwaitValue()

        assertEquals(listDatabaseDisc.size, result)
    }

    @Test
    fun test_CountFormatMediaList() {
        val result = informationViewModel.countFormatMediaList.getOrAwaitValue()
        val size = listDatabaseDisc.groupBy { it.formatMedia }.size

        assertEquals(size, result.size)
    }
}