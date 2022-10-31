package com.diego.discoteca.ui.discDetail

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.R
import com.diego.discoteca.database.DatabaseDisc
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
    fun discDetail_Manually_DisplayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseManually))

        val bundle = DiscDetailFragmentArgs(discDatabaseManually.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_by))
            .check(matches(withText(context.getString(R.string.disc_add_manually))))

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_name))
            .check(matches(withText(discDatabaseManually.name)))

        onView(withId(R.id.disc_title)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_title))
            .check(matches(withText(discDatabaseManually.title)))

        val countryYear = getCountryYear(discDatabaseManually)
        onView(withId(R.id.disc_country_year)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_country_year))
            .check(matches(withText(countryYear)))

        onView(withId(R.id.disc_format_media)).check(matches(not(isDisplayed())))

        val format = getDiscFormat(discDatabaseManually)
        onView(withId(R.id.disc_format)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_format)).check(matches(withText(format)))

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun discDetail_Scan_DisplayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseScan))

        val bundle = DiscDetailFragmentArgs(discDatabaseScan.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_by))
            .check(matches(withText(context.getString(R.string.disc_add_scan))))

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_name))
            .check(matches(withText(discDatabaseScan.name)))

        onView(withId(R.id.disc_title)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_title))
            .check(matches(withText(discDatabaseScan.title)))

        val countryYear = getCountryYear(discDatabaseScan)
        onView(withId(R.id.disc_country_year)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_country_year)).check(matches(withText(countryYear)))

        onView(withId(R.id.disc_format_media)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_format_media)).check(matches(withText(discDatabaseScan.formatMedia)))

        val format = getDiscFormat(discDatabaseScan)
        onView(withId(R.id.disc_format)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_format)).check(matches(withText(format)))

        onView(withId(R.id.tv_barcode)).check(matches(isDisplayed()))

        onView(withId(R.id.barcode)).check(matches(isDisplayed()))
        onView(withId(R.id.barcode)).check(matches(withText(discDatabaseScan.barcode)))

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun discDetail_Search_DisplayedInUi() {
        discsRepository.setDatabaseDisc(listOf(discDatabaseSearch))

        val bundle = DiscDetailFragmentArgs(discDatabaseSearch.id).toBundle()
        launchFragmentInContainer<DiscDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.tv_disc_add_by)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_by))
            .check(matches(withText(context.getString(R.string.disc_add_search))))

        onView(withId(R.id.disc_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.disc_name)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_name))
            .check(matches(withText(discDatabaseSearch.name)))

        onView(withId(R.id.disc_title)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_title))
            .check(matches(withText(discDatabaseSearch.title)))

        val countryYear = getCountryYear(discDatabaseSearch)
        onView(withId(R.id.disc_country_year)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_country_year)).check(matches(withText(countryYear)))

        onView(withId(R.id.disc_format_media)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_format_media)).check(matches(withText(discDatabaseSearch.formatMedia)))

        val format = getDiscFormat(discDatabaseSearch)
        onView(withId(R.id.disc_format)).check(matches(isDisplayed()))
        onView(withId(R.id.disc_format)).check(matches(withText(format)))

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_ok)).check(matches(isDisplayed()))
    }

    private fun getCountryYear(databaseDisc: DatabaseDisc) =
        when {
            databaseDisc.country.isEmpty() && databaseDisc.year.isEmpty() -> context.getString(R.string.not_specified)
            databaseDisc.country.isEmpty() && databaseDisc.year.isNotEmpty() -> databaseDisc.year
            databaseDisc.country.isNotEmpty() && databaseDisc.year.isEmpty() ->
                "${databaseDisc.country} - ${context.getString(R.string.not_specified)}"
            else -> "${databaseDisc.country} - ${databaseDisc.year}"
        }

    private fun getDiscFormat(databaseDisc: DatabaseDisc) =
        databaseDisc.format.ifEmpty { context.getString(R.string.media_undefined) }

}