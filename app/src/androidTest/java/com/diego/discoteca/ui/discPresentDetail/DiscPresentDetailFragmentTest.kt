package com.diego.discoteca.ui.discPresentDetail

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
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class DiscPresentDetailFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
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

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun disc_manually_displayInUi() = runTest {
        val discAdd = DiscAdd(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = databaseDisc1.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscPresentDetailFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscPresentDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val addBy = context.getString(R.string.disc_add_manually)
        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(addBy)))
        }

        onView(withId(R.id.tv_answer)).check(matches(not(isDisplayed())))

        onView(withId(R.id.disc_present_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_present_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_present_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        val countryYear = context.getCountryYear(databaseDisc1)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_present_format_media)).check(matches(not(isDisplayed())))

        val format = context.getDiscFormat(databaseDisc1)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(format)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_search)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_cancel_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun disc_search_displayInUi() = runTest {
        val discAdd = DiscAdd(
            id = databaseDisc3.id,
            name = databaseDisc3.name,
            title = databaseDisc3.title,
            year = databaseDisc3.year,
            addBy = databaseDisc3.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc3.nameNormalize,
            title = databaseDisc3.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscPresentDetailFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscPresentDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val addBy = context.getString(R.string.disc_add_search)
        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(addBy)))
        }

        onView(withId(R.id.tv_answer)).check(matches(not(isDisplayed())))

        onView(withId(R.id.disc_present_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_present_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_present_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        val countryYear = context.getCountryYear(databaseDisc3)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_present_format_media)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(databaseDisc3.formatMedia)))
        }

        val format = context.getDiscFormat(databaseDisc3)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(format)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_search)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_cancel_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun disc_search_database_manually_displayInUi() = runTest {
        val discAdd = DiscAdd(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscPresentDetailFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscPresentDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val addBy = context.getString(R.string.disc_add_manually)
        onView(withId(R.id.tv_disc_add_by)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(addBy)))
        }

        val answer = context.getString(R.string.answer_search)
        onView(withId(R.id.tv_answer)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(answer)))
        }

        onView(withId(R.id.disc_present_cover_image)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_disc_present_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_present_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        val countryYear = context.getCountryYear(databaseDisc1)
        onView(withId(R.id.tv_disc_result_country_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(countryYear)))
        }

        onView(withId(R.id.tv_disc_present_format_media)).check(matches(not(isDisplayed())))

        val formatMedia = context.getDiscFormat(databaseDisc1)
        onView(withId(R.id.tv_disc_result_format)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(formatMedia)))
        }

        onView(withId(R.id.tv_barcode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_barcode)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_search)).check(matches(isDisplayed()))
        onView(withId(R.id.button_cancel_ok)).check(matches(isDisplayed()))
    }

    @Test
    fun disc_manually_from_discPresent_popBackStack() = runTest {
        val discAdd = DiscAdd(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = databaseDisc1.addBy
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentDetailFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            // From discPresentFragment -> discPresentDetailFragment
            navController.navigate(R.id.discPresentFragment)
            navController.navigate(R.id.discPresentDetailFragment)
        }

        onView(withId(R.id.button_cancel_ok)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discPresentFragment)
    }

    @Test
    fun disc_search_database_manually_navToDiscResultSearch() = runTest {
        val discAdd = DiscAdd(
            id = databaseDisc1.id,
            name = databaseDisc1.name,
            title = databaseDisc1.title,
            year = databaseDisc1.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            name = databaseDisc1.nameNormalize,
            title = databaseDisc1.titleNormalize,
            year = discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentDetailFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discPresentDetailFragment)
        }

        onView(withId(R.id.button_search)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discResultSearchFragment)
    }
}