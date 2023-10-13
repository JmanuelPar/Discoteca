package com.diego.discoteca.ui.discDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.asDomainModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DiscDetailViewModelTest {

    private lateinit var discDetailViewModel: DiscDetailViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>

    private lateinit var databaseDisc1: DatabaseDisc

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        discsRepository = FakeDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        databaseDisc1 = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        listDatabaseDisc = listOf(databaseDisc1)

        discsRepository.setDatabaseDisc(listDatabaseDisc)

        discDetailViewModel = DiscDetailViewModel(
            discsRepository,
            databaseDisc1.id
        )
    }

    @Test
    fun test_GetDisc() {
        val result = discDetailViewModel.getDisc().getOrAwaitValue()

        assertEquals(databaseDisc1.asDomainModel(), result)
    }

    @Test
    fun test_ButtonOkClicked() {
        discDetailViewModel.buttonOkClicked()
        val result = discDetailViewModel.buttonOk.getOrAwaitValue()

        assertEquals(true, result)
    }
}