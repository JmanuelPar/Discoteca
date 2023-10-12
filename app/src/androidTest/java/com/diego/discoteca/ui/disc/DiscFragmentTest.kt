package com.diego.discoteca.ui.disc

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.MyViewAction
import com.diego.discoteca.R
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import com.diego.discoteca.util.UIText
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@MediumTest
@RunWith(AndroidJUnit4::class)
class DiscFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var discDatabaseManually: DatabaseDisc
    private lateinit var discDatabaseScan: DatabaseDisc
    private lateinit var discDatabaseSearch: DatabaseDisc
    private lateinit var list: List<DatabaseDisc>

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabaseManually = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabaseScan = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabaseSearch = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        list = listOf(
            discDatabaseManually,
            discDatabaseScan,
            discDatabaseSearch
        )
        discsRepository.setDatabaseDisc(list)

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun recyclerView_scrollToPosition() {
        val bundle = DiscFragmentArgs(UIText.NoDisplay, -1L).toBundle()
        launchFragmentInContainer<DiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val position = list.indexOf(discDatabaseSearch)
        onView(withId(R.id.rv_list_discs))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position)
            )

        onView(withText(discDatabaseSearch.name)).check(matches(isDisplayed()))
    }

    @Test
    fun navigate_to_update() {
        val navController = TestNavHostController(context)
        val bundle = DiscFragmentArgs(UIText.NoDisplay, -1L).toBundle()
        val scenario = launchFragmentInContainer<DiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.rv_list_discs))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    MyViewAction.clickChildViewWithId(R.id.img_overflow_menu)
                )
            )

        sleep(2000)

        val modify = context.getString(R.string.modify)
        // Click modify in PopupMenu
        onView(withText(modify)).perform(click())

        // Click modify in Material Alert Dialog
        onView(withText(modify)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.updateDiscFragment)
    }

    @Test
    fun navigate_to_disc_detail() {
        val navController = TestNavHostController(context)
        val bundle = DiscFragmentArgs(UIText.NoDisplay, -1L).toBundle()
        val scenario = launchFragmentInContainer<DiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.rv_list_discs))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, click()
                )
            )

        assertEquals(navController.currentDestination?.id, R.id.discDetailFragment)
    }
}