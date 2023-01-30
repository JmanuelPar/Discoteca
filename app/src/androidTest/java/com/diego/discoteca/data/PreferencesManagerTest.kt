package com.diego.discoteca.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.TestAndroidDataStore
import com.diego.discoteca.TestAndroidDataStore.MODE_LIGHT
import com.diego.discoteca.TestAndroidDataStore.MODE_NIGHT
import com.diego.discoteca.TestAndroidDataStore.MODE_SYSTEM
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PreferencesManagerTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        preferencesManager = TestAndroidDataStore.providePreferencesManager(context)
    }

    @After
    fun reset() {
        TestAndroidDataStore.reset()
    }

    @Test
    fun test_light_updateNightMode() = runTest {
        preferencesManager.updateNightMode(MODE_LIGHT)
        val mode = preferencesManager.nightModeFlow.first()

        assertEquals(MODE_LIGHT, mode)
    }

    @Test
    fun test_night_updateNightMode() = runTest {
        preferencesManager.updateNightMode(MODE_NIGHT)
        val mode = preferencesManager.nightModeFlow.first()

        assertEquals(MODE_NIGHT, mode)
    }

    @Test
    fun test_system_updateNightMode() = runTest {
        // Default : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        val mode = preferencesManager.nightModeFlow.first()

        assertEquals(MODE_SYSTEM, mode)
    }

    @Test
    fun test_linear_updateGridMode() = runTest {
        // Default : false -> linearLayout
        val isLinear = preferencesManager.gridModeFlow.first()

        assertEquals(false, isLinear)
    }

    @Test
    fun test_grid_updateGridMode() = runTest {
        preferencesManager.updateGridMode(true)
        val isGrid = preferencesManager.gridModeFlow.first()

        assertEquals(true, isGrid)
    }

    @Test
    fun test_name_updateSortOrder() = runTest {
        // Default : SortOrder.BY_NAME
        val sortOrder = preferencesManager.sortOrderFlow.first().sortOrder

        assertEquals(SortOrder.BY_NAME, sortOrder)
    }

    @Test
    fun test_title_updateSortOrder() = runTest {
        preferencesManager.updateSortOrder(SortOrder.BY_TITLE)
        val sortOrder = preferencesManager.sortOrderFlow.first().sortOrder

        assertEquals(SortOrder.BY_TITLE, sortOrder)
    }

    @Test
    fun test_year_updateSortOrder() = runTest {
        preferencesManager.updateSortOrder(SortOrder.BY_YEAR)
        val sortOrder = preferencesManager.sortOrderFlow.first().sortOrder

        assertEquals(SortOrder.BY_YEAR, sortOrder)
    }
}