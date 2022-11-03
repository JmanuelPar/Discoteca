package com.diego.discoteca.ui.information

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.BuildConfig
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.R
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.Destination
import com.diego.discoteca.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InformationFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var discDatabaseManually: DatabaseDisc
    private lateinit var discDatabaseScan: DatabaseDisc
    private lateinit var discDatabaseSearch: DatabaseDisc

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun init_NoDisc_DisplayInUi() {
        launchFragmentInContainer<InformationFragment>(themeResId = R.style.Theme_Discoteca)

        onView(withId(R.id.button_search_scan_database)).check(matches(not(isDisplayed())))

        onView(withId(R.id.icon_catalog)).check(matches(isDisplayed()))

        onView(withId(R.id.nb_disc_total)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.no_disc))))
        }

        onView(withId(R.id.rv_list_format_media)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            matches(isDisplayed())

            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.my_version)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.app_version))))
        }

        onView(withId(R.id.app_version)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE)))
        }
    }

    @Test
    fun init_Disc_DisplayInUi() = runTest {
        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabaseManually = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabaseScan = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabaseSearch = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)

        discsRepository.setDatabaseDisc(
            listOf(
                discDatabaseManually,
                discDatabaseScan,
                discDatabaseSearch
            )
        )

        val countDiscs = discsRepository.countAllDiscs.first()
        val countFormatMediaList = discsRepository.countFormatMediaList.first()

        launchFragmentInContainer<InformationFragment>(themeResId = R.style.Theme_Discoteca)

        onView(withId(R.id.button_search_scan_database)).check(matches(isDisplayed()))

        onView(withId(R.id.icon_catalog)).check(matches(isDisplayed()))

        onView(withId(R.id.nb_disc_total)).apply {
            check(matches(isDisplayed()))
            check(
                matches(
                    withText(
                        context.resources.getQuantityString(
                            R.plurals.plural_nb_disc_discoteca,
                            countDiscs,
                            countDiscs
                        )
                    )
                )
            )
        }

        onView(withId(R.id.rv_list_format_media)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            matches(isDisplayed())

            val recyclerView = view as RecyclerView
            assertEquals(countFormatMediaList.size, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.my_version)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.app_version))))
        }

        onView(withId(R.id.app_version)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE)))
        }
    }

    @Test
    fun navTo_ScanBarcodeFragment() {
        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabaseManually = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabaseScan = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabaseSearch = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)

        discsRepository.setDatabaseDisc(
            listOf(
                discDatabaseManually,
                discDatabaseScan,
                discDatabaseSearch
            )
        )

        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<InformationFragment>(themeResId = R.style.Theme_Discoteca)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.button_search_scan_database)).perform(click())

        val navDirections =
            InformationFragmentDirections.actionInfoFragmentToScanBarcodeFragment(Destination.DATABASE)
        verify(navController).navigate(navDirections)
    }
}