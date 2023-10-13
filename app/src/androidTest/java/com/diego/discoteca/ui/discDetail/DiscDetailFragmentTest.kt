package com.diego.discoteca.ui.discDetail

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.diego.discoteca.*
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class DiscDetailFragmentTest {

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
        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabaseManually = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabaseScan = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabaseSearch = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun discDetail_manually_displayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseManually))

        val bundle = DiscDetailFragmentArgs(discDatabaseManually.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.disc_add_manually))))
        }

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseManually.name)))
        }

        onView(withId(R.id.disc_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseManually.title)))
        }

        val countryYear = context.getCountryYear(discDatabaseManually)
        onView(withId(R.id.disc_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.disc_format_media)).check(matches(not(isDisplayed())))

        val format = context.getDiscFormat(discDatabaseManually)
        onView(withId(R.id.disc_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(format)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun discDetail_scan_displayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseScan))

        val bundle = DiscDetailFragmentArgs(discDatabaseScan.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.disc_add_scan))))
        }

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseScan.name)))
        }

        onView(withId(R.id.disc_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseScan.title)))
        }

        val countryYear = context.getCountryYear(discDatabaseScan)
        onView(withId(R.id.disc_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.disc_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseScan.formatMedia)))
        }

        val format = context.getDiscFormat(discDatabaseScan)
        onView(withId(R.id.disc_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(format)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(isDisplayed()))

        onView(withId(R.id.barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseScan.barcode)))
        }

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun discDetail_search_displayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseSearch))

        val bundle = DiscDetailFragmentArgs(discDatabaseSearch.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.disc_add_search))))
        }

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseSearch.name)))
        }

        onView(withId(R.id.disc_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseSearch.title)))
        }

        val countryYear = context.getCountryYear(discDatabaseSearch)
        onView(withId(R.id.disc_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.disc_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discDatabaseSearch.formatMedia)))
        }

        val format = context.getDiscFormat(discDatabaseSearch)
        onView(withId(R.id.disc_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(format)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun onButtonOkClicked_from_disc_popBackStack() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseManually))

        val navController = TestNavHostController(context)
        val bundle = DiscDetailFragmentArgs(discDatabaseManually.id).toBundle()
        val scenario = launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            // From DiscFragment (start) -> DiscDetailFragment
            navController.setCurrentDestination(R.id.discDetailFragment, bundle)
        }

        onView(withId(R.id.button_ok)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }

    @Test
    fun onButtonOkClicked_from_discResultScan_popBackStack() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseScan))

        val navController = TestNavHostController(context)
        val bundle = DiscDetailFragmentArgs(discDatabaseScan.id).toBundle()
        val scenario = launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            // From DiscResultScanFragment(Destination.DATABASE) -> DiscDetailFragment
            navController.navigate(R.id.discResultScanFragment)
            navController.navigate(R.id.discDetailFragment)
        }

        onView(withId(R.id.button_ok)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discResultScanFragment)
    }
}