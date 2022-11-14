package com.diego.discoteca.ui.disc

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.*
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.asDomainModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class DiscViewModelTest {

    private lateinit var discViewModel: DiscViewModel
    private lateinit var discsRepository: FakeDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>

    private lateinit var preferencesManager: PreferencesManager
    private var idAdded = -1L
    private var uiText = UIText.NoDisplay

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

        val context = Mockito.mock(Context::class.java)
        `when`(context.applicationContext).thenReturn(context)

        val dataStore = TestDataStore.provideTestDataStore(context)
        preferencesManager = PreferencesManager(dataStore)
    }

    @Test
    fun test_OnId_Init() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        val result = discViewModel.id.getOrAwaitValue()

        assertEquals(idAdded, result)
    }

    @Test
    fun test_OnShowSnackBar_Init() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        val result = discViewModel.showSnackBar.getOrAwaitValue()

        assertEquals(uiText, result)
    }

    @Test
    fun test_ListDiscs_SortByName() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )
        // Default : SortOrder.BY_NAME
        val result = discViewModel.listDiscs.getOrAwaitValue()

        assertEquals(listDatabaseDisc.sortedBy {
            it.name
        }.asDomainModel(), result)
    }

    @Test
    fun test_ButtonDeleteDiscClicked() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        discViewModel.buttonDeleteDiscClicked(databaseDisc1.id)
        val result = discViewModel.buttonDeleteDisc.getOrAwaitValue()

        assertEquals(databaseDisc1.id, result)
    }

    @Test
    fun test_YesDeleteClicked() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        discViewModel.yesDeleteDiscClicked(databaseDisc1.id)
        val result = discViewModel.showSnackBar.getOrAwaitValue()

        assertEquals(UIText.DiscDeleted, result)
    }

    @Test
    fun test_ButtonUpdateDiscClicked() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        discViewModel.buttonUpdateDiscClicked(databaseDisc2.id)
        val result = discViewModel.buttonUpdateDisc.getOrAwaitValue()

        assertEquals(databaseDisc2.id, result)
    }

    @Test
    fun test_YesUpdateClicked() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        discViewModel.yesUpdateDiscClicked(databaseDisc2.id)
        val result = discViewModel.navigateToUpdateDisc.getOrAwaitValue()

        assertEquals(databaseDisc2.id, result)
    }

    @Test
    fun test_DiscUpdated_ScrollToPosition() {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            UIText.DiscUpdated,
            databaseDisc3.id
        )

        val position = listDatabaseDisc.indexOf(databaseDisc3)

        discViewModel.scrollToPosition(listDatabaseDisc.asDomainModel())
        val result = discViewModel.scrollToPosition.getOrAwaitValue()

        assertEquals(position, result)
    }

    @Test
    fun test_UpdateSearch() = runTest {
        discViewModel = DiscViewModel(
            discsRepository,
            preferencesManager,
            uiText,
            idAdded
        )

        val query = "Update_search"
        discViewModel.updateSearch(query)
        val result = discViewModel.searchQueryFlow.first()

        assertEquals(query, result)
    }
}