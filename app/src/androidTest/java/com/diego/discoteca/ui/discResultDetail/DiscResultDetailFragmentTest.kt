package com.diego.discoteca.ui.discResultDetail

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
import com.diego.discoteca.*
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DiscResultDetailFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository

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
    fun init_addBy_scan_noPresent() {
        /* From DiscResultScanFragment, AddBy by Scan
           Nothing present in database -> isPresentByManually, isPresentBySearch, isPresentByScan : false
           So, button add and button cancel appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "barcode_1",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SCAN
        )

        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_present)).check(matches(not(isDisplayed())))

        onView(withId(R.id.layout_disc_light)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_answer)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.answer_add))))
        }

        onView(withId(R.id.disc_result_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_result_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.name)))
        }

        onView(withId(R.id.tv_disc_result_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.title)))
        }

        val countryYear = context.getCountryYear(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_result_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.formatMedia)))
        }

        val discFormat = context.getDiscFormat(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discFormat)))
        }

        onView(withId(R.id.tv_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.barcode))))
        }

        onView(withId(R.id.tv_disc_result_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.barcode)))
        }

        onView(withId(R.id.button_yes)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.add))))
        }

        onView(withId(R.id.button_no)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.cancel))))
        }
    }

    @Test
    fun init_addBy_search_noPresent() {
        /* From DiscResultSearchFragment, AddBy by Search
           Nothing present in database -> isPresentByManually, isPresentBySearch, isPresentByScan : false
           So, button add and button cancel appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SEARCH
        )

        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_present)).check(matches(not(isDisplayed())))

        onView(withId(R.id.layout_disc_light)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_answer)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.answer_add))))
        }

        onView(withId(R.id.disc_result_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_result_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.name)))
        }

        onView(withId(R.id.tv_disc_result_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.title)))
        }

        val countryYear = context.getCountryYear(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_result_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.formatMedia)))
        }

        val discFormat = context.getDiscFormat(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discFormat)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_disc_result_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_yes)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.add))))
        }

        onView(withId(R.id.button_no)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.cancel))))
        }
    }

    @Test
    fun init_addBy_scan_presentSearch() {
        /* From DiscResultScanFragment, AddBy by Scan
           Present in database -> isPresentBySearch = true
           So, button update and button cancel appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "barcode_1",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        disc.isPresentBySearch = true
        disc.discLight = DiscLight(
            id = 1L,
            name = "name_database_1",
            title = "title_database_1",
            year = "year_database_1",
            country = "country_database_1",
            format = "format_database_1",
            formatMedia = "formatMedia_database_1",
        )

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SCAN
        )

        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_present)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.disc_present_search))))
        }

        onView(withId(R.id.layout_disc_light)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_light_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.discLight!!.name)))
        }

        onView(withId(R.id.tv_disc_light_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.discLight!!.title)))
        }

        val discLightCountryYear =
            context.getDiscLightCountryYear(discResultDetail.disc.discLight!!)
        onView(withId(R.id.tv_disc_light_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discLightCountryYear)))
        }

        onView(withId(R.id.tv_disc_light_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.discLight!!.formatMedia)))
        }

        val discLightFormat = context.getDiscLightFormat(discResultDetail.disc)
        onView(withId(R.id.tv_disc_light_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discLightFormat)))
        }

        onView(withId(R.id.tv_answer)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.answer_update))))
        }

        onView(withId(R.id.disc_result_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_result_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.name)))
        }

        onView(withId(R.id.tv_disc_result_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.title)))
        }

        val countryYear = context.getCountryYear(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_result_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.formatMedia)))
        }

        val discFormat = context.getDiscFormat(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discFormat)))
        }

        onView(withId(R.id.tv_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.barcode))))
        }

        onView(withId(R.id.tv_disc_result_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.barcode)))
        }

        onView(withId(R.id.button_yes)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.update))))
        }

        onView(withId(R.id.button_no)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.cancel))))
        }
    }

    @Test
    fun init_addBy_search_presentScan() {
        /* From DiscResultSearchFragment, AddBy by Search
           Present in database -> isPresentByScan = true
           So, button ok appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        disc.isPresentByScan = true

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SEARCH
        )

        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_present)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.disc_present_scan))))
        }

        onView(withId(R.id.layout_disc_light)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_answer)).check(matches(not(isDisplayed())))

        onView(withId(R.id.disc_result_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_result_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.name)))
        }

        onView(withId(R.id.tv_disc_result_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.title)))
        }

        val countryYear = context.getCountryYear(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_result_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultDetail.disc.formatMedia)))
        }

        val discFormat = context.getDiscFormat(discResultDetail.disc)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discFormat)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_disc_result_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_yes)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(context.getString(R.string.ok))))
        }

        onView(withId(R.id.button_no)).check(matches(not(isDisplayed())))
    }

    @Test
    fun onButtonNoClicked_popBackStack() {
        /* From DiscResultScanFragment, AddBy by Scan
           Nothing present in database -> isPresentByManually, isPresentBySearch, isPresentByScan : false
           So, button add and button cancel appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "barcode_1",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SCAN
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        val scenario = launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            // From DiscResultScanFragment -> DiscResultDetailFragment
            navController.navigate(R.id.discResultScanFragment)
            navController.navigate(R.id.discResultDetailFragment)
        }

        // Button no (cancel) -> popBackStack
        onView(withId(R.id.button_no)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discResultScanFragment)
    }

    @Test
    fun onButtonYesClicked_popBackStack() {
        /* From DiscResultSearchFragment, AddBy by Search
           Present in database -> isPresentByScan = true
           So, button ok appears */
        val disc = Disc(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_1",
            formatMedia = "formatMedia_1",
            coverImage = "url_coverImage_1",
            barcode = "",
            idDisc = 2,
            addBy = AddBy.NONE
        )

        disc.isPresentByScan = true

        val discResultDetail = DiscResultDetail(
            disc = disc,
            addBy = AddBy.SEARCH
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultDetailFragmentArgs(discResultDetail).toBundle()
        val scenario = launchFragmentInContainer<DiscResultDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            // From DiscResultSearchFragment -> DiscResultDetailFragment
            navController.navigate(R.id.discResultSearchFragment)
            navController.navigate(R.id.discResultDetailFragment)
        }

        // Button yes (ok) -> popBackStack
        onView(withId(R.id.button_yes)).perform(click())

        assertEquals(navController.currentDestination!!.id, R.id.discResultSearchFragment)
    }
}