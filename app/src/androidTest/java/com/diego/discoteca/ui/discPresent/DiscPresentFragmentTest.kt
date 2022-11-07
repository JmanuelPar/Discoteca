package com.diego.discoteca.ui.discPresent

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.DatabaseDiscFactory
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.R
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DiscPresentFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var listDatabaseDisc: List<DatabaseDisc>
    private lateinit var databaseDisc1: DatabaseDisc
    private lateinit var databaseDisc2: DatabaseDisc
    private lateinit var databaseDisc3: DatabaseDisc

    private val discAddM = DiscAdd(
        name = "name_3",
        title = "title_3",
        year = "year_3",
        addBy = AddBy.MANUALLY
    )

    private val discAddS = DiscAdd(
        name = "name_2",
        title = "title_2",
        year = "year_2",
        addBy = AddBy.SEARCH
    )

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
    fun disc_present_manually_displayInUi() = runTest {
        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_3",
            title = "title_normalize_3",
            year = discAddM.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddM
        )

        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val nbTotal = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_discoteca,
            listDb.size,
            listDb.size
        )

        onView(withId(R.id.tv_nb_disc_present_discoteca)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbTotal)))
        }

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_present)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            matches(isDisplayed())

            val recyclerView = view as RecyclerView
            assertEquals(listDb.size, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.layout_user_disc_add)).check(matches(isDisplayed()))

        val answer = context.getString(R.string.answer_add_still)
        onView(withId(R.id.tv_answer_still)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(answer)))
        }

        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddM.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddM.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddM.year)))
        }

        val add = context.getString(R.string.add)
        onView(withId(R.id.button_add)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(add)))
        }

        val cancel = context.getString(R.string.cancel)
        onView(withId(R.id.button_cancel)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(cancel)))
        }
    }

    @Test
    fun disc_present_search_displayInUi() = runTest {
        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAddS.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddS
        )

        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        val nbTotal = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_discoteca,
            listDb.size,
            listDb.size
        )

        onView(withId(R.id.tv_nb_disc_present_discoteca)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbTotal)))
        }

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_present)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            matches(isDisplayed())

            val recyclerView = view as RecyclerView
            assertEquals(listDb.size, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.layout_user_disc_add)).check(matches(isDisplayed()))

        val answer = context.getString(R.string.answer_search_still)
        onView(withId(R.id.tv_answer_still)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(answer)))
        }

        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddS.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddS.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAddS.year)))
        }

        val search = context.getString(R.string.search)
        onView(withId(R.id.button_add)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(search)))
        }

        val cancel = context.getString(R.string.cancel)
        onView(withId(R.id.button_cancel)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(cancel)))
        }
    }

    @Test
    fun disc_present_add_manually_navToDisc() = runTest {
        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_3",
            title = "title_normalize_3",
            year = discAddM.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddM
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discPresentFragment)
        }

        onView(withId(R.id.button_add)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }

    @Test
    fun disc_present_add_search_navToDiscResultSearch() = runTest {
        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAddS.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddS
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discPresentFragment)
        }

        onView(withId(R.id.button_add)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discResultSearchFragment)
    }

    @Test
    fun disc_present_add_search_buttonOk_navToDisc() = runTest {
        /* We have the same disc added manually and by search
           databaseDisc3 : AddBy.SEARCH, name_2 */
        val newDatabaseDiscM = DatabaseDisc(
            id = 4L,
            name = "name_2",
            title = "title_2",
            year = "year_2",
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = "name_normalize_2",
            titleNormalize = "title_normalize_2"
        )

        discsRepository.setDatabaseDisc(listOf(newDatabaseDiscM))

        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAddS.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddS
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discPresentFragment)
        }

        onView(withId(R.id.button_ok)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }

    @Test
    fun disc_present_add_search_cancel_navToDisc() = runTest {
        val listDb = discsRepository.getListDiscDbPresent(
            name = "name_normalize_2",
            title = "title_normalize_2",
            year = discAddS.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAddS
        )

        val navController = TestNavHostController(context)
        val bundle = DiscPresentFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscPresentFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discPresentFragment)
        }

        onView(withId(R.id.button_cancel)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }
}