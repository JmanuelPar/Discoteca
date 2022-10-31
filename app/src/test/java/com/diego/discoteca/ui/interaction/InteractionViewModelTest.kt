package com.diego.discoteca.ui.interaction

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeDiscsRepository
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.AddBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InteractionViewModelTest {

    private lateinit var interactionViewModel: InteractionViewModel
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
    fun test_NumberDiscs() {
        interactionViewModel = InteractionViewModel(
            discsRepository
        )

        val result = interactionViewModel.numberDiscs.getOrAwaitValue()

        assertEquals(listDatabaseDisc.size, result)
    }

    @Test
    fun test_UpdateProgressLinearGDriveUpload_Init() {
        interactionViewModel = InteractionViewModel(discsRepository)

        val result = interactionViewModel.progressLinearGDriveUpload.getOrAwaitValue()

        assertEquals(false, result)
    }

    @Test
    fun test_UpdateProgressLinearGDriveDownload_Init() {
        interactionViewModel = InteractionViewModel(discsRepository)

        val result = interactionViewModel.progressLinearGDriveDownload.getOrAwaitValue()

        assertEquals(false, result)
    }

    @Test
    fun test_DriveLogOutClicked() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.driveLogOutClicked()
        val result = interactionViewModel.driveLogOutClicked.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_DriveDisconnectClicked() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.driveDisconnectClicked()
        val result = interactionViewModel.driveDisconnectClicked.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_UpdateIconGDriveUpload() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.updateIconGDriveUpload(true)
        val result = interactionViewModel.stateIconGDriveUpload.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_UpdateIconGDriveDownload() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.updateIconGDriveDownload(true)
        val result = interactionViewModel.stateIconGDriveDownload.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_IconGDriveUploadClicked() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.iconGDriveUploadClicked()
        val result = interactionViewModel.iconGDriveUploadClicked.getOrAwaitValue()

        assertEquals(true, result)
    }

    @Test
    fun test_IconGDriveDownloadClicked() {
        interactionViewModel = InteractionViewModel(discsRepository)

        interactionViewModel.iconGDriveDownloadClicked()
        val result = interactionViewModel.iconGDriveDownloadClicked.getOrAwaitValue()

        assertEquals(true, result)
    }
}