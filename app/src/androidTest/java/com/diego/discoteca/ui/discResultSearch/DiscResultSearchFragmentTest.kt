package com.diego.discoteca.ui.discResultSearch

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
import com.diego.discoteca.DiscApiFactory
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.R
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscAdd
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DiscResultSearchFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var discApi1: Disc
    private lateinit var discApi2: Disc
    private lateinit var discApi3: Disc
    private lateinit var databaseDisc1Sc: DatabaseDisc
    private lateinit var databaseDisc2Se: DatabaseDisc
    private lateinit var databaseDisc3Man: DatabaseDisc

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
        val discApiFactory = DiscApiFactory()
        val databaseDiscFactory = DatabaseDiscFactory()
        discApi1 = discApiFactory.createDiscApi()
        discApi2 = discApiFactory.createDiscApi()
        discApi3 = discApiFactory.createDiscApi()

        databaseDisc1Sc = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        databaseDisc2Se = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        databaseDisc3Man = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun search_disc_in_api_displayInUi() = runTest {
        // From addDiscFragment
        discsRepository.setDiscApi(listOf(discApi1))

        val discAdd = DiscAdd(
            name = discApi1.name,
            title = discApi1.title,
            year = discApi1.year,
            addBy = AddBy.SEARCH
        )

        // Empty
        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(not(isDisplayed())))

        val result = context.resources.getQuantityString(
            R.plurals.plural_total_api_result,
            1,
            1
        )
        onView(withId(R.id.tv_disc_search_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.layout_disc_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.year)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))

        val chooseDisc = context.getString(R.string.choose_disc)
        onView(withId(R.id.tv_choose_disc)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(chooseDisc)))
        }

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                matches(isDisplayed())

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(1, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_disc_api_not_found_displayInUi() = runTest {
        // From addDiscFragment
        discsRepository.setDiscApi(emptyList())

        val discAdd = DiscAdd(
            name = discApi1.name,
            title = discApi1.title,
            year = discApi1.year,
            addBy = AddBy.SEARCH
        )

        // Empty
        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(isDisplayed()))

        onView(withId(R.id.img_disc)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_search_result)).check(matches(isDisplayed()))

        val message = context.getString(R.string.no_search_result_recommendation)
        onView(withId(R.id.tv_no_search_result_recommendation)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(message)))
        }

        onView(withId(R.id.tv_disc_search_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_disc_add)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(not(isDisplayed())))

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(0, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_disc_in_api_disc_in_database_add_scan_displayInUi() = runTest {
        /* From DiscPresentFragment (only disc present in database added by scan / search)
           name_1 -> databaseDisc1Sc.name, discApi1.name
           databaseDisc1Sc -> add by scan */
        discsRepository.setDiscApi(listOf(discApi1))
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discAdd = DiscAdd(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(not(isDisplayed())))

        val result = context.resources.getQuantityString(
            R.plurals.plural_total_api_result,
            1,
            1
        )
        onView(withId(R.id.tv_disc_search_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.layout_disc_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.year)))
        }

        val nb = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_scan,
            1,
            1
        )
        onView(withId(R.id.tv_disc_present_scan)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nb)))
        }
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))

        val chooseDisc = context.getString(R.string.choose_disc)
        onView(withId(R.id.tv_choose_disc)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(chooseDisc)))
        }

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                matches(isDisplayed())

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(1, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_disc_in_api_disc_in_database_add_search_displayInUi() = runTest {
        /* From DiscPresentFragment (only disc present in database added by scan / search)
           name_2 -> databaseDisc2Se.name, discApi2.name
           databaseDisc2Sc -> add by search */
        discsRepository.setDiscApi(listOf(discApi2))
        discsRepository.setDatabaseDisc(listOf(databaseDisc2Se))

        val discAdd = DiscAdd(
            name = "name_2",
            title = "title_2",
            year = "year_2",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_2",
            "title_normalize_2",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(not(isDisplayed())))

        val result = context.resources.getQuantityString(
            R.plurals.plural_total_api_result,
            1,
            1
        )
        onView(withId(R.id.tv_disc_search_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.layout_disc_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.year)))
        }

        val nb = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_search,
            1,
            1
        )
        onView(withId(R.id.tv_disc_present_search)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nb)))
        }
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))

        val chooseDisc = context.getString(R.string.choose_disc)
        onView(withId(R.id.tv_choose_disc)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(chooseDisc)))
        }

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                matches(isDisplayed())

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(1, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_disc_in_api_disc_in_database_add_more_displayInUi() = runTest {
        /* From DiscPresentFragment (disc present in database added by scan and search)
           name_1 -> databaseDisc1Sc.name, discApi1.name
           databaseDisc1Sc -> add by scan */
        val databaseDisc1Se = DatabaseDisc(
            id = 2L,
            name = "name_1",
            title = "title_1",
            year = "year_1",
            country = "country_1",
            format = "format_2",
            formatMedia = "format_media_2",
            coverImage = "url_coverImage_2",
            barcode = "",
            idDisc = 3,
            addBy = AddBy.SEARCH,
            nameNormalize = "name_normalize_1",
            titleNormalize = "title_normalize_1"
        )

        val discApi1other = discApi1.copy(
            format = "format_2",
            formatMedia = "formatMedia_2",
            coverImage = "url_coverImage_2",
            barcode = "barcode_2",
            idDisc = 3
        )

        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc, databaseDisc1Se))
        discsRepository.setDiscApi(listOf(discApi1, discApi1other))

        val discAdd = DiscAdd(
            name = discApi1.name,
            title = discApi1.title,
            year = discApi1.year,
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(not(isDisplayed())))

        val result = context.resources.getQuantityString(
            R.plurals.plural_total_api_result,
            2,
            2
        )
        onView(withId(R.id.tv_disc_search_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.layout_disc_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.year)))
        }

        val nbSe = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_search,
            1,
            1
        )
        onView(withId(R.id.tv_disc_present_search)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbSe)))
        }

        val nbSc = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_scan,
            1,
            1
        )
        onView(withId(R.id.tv_disc_present_scan)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbSc)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))

        val chooseDisc = context.getString(R.string.choose_disc)
        onView(withId(R.id.tv_choose_disc)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(chooseDisc)))
        }

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                matches(isDisplayed())

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(2, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_disc_in_api_disc_in_database_add_manually_displayInUi() = runTest {
        /* From DiscPresentDetailFragment (we make a search and disc is present in database added manually)
           name_3 -> databaseDisc3Man.name, discApi3.name
           databaseDisc3Man -> add manually */
        discsRepository.setDiscApi(listOf(discApi3))
        discsRepository.setDatabaseDisc(listOf(databaseDisc3Man))

        val discAdd = DiscAdd(
            name = discApi3.name,
            title = discApi3.title,
            year = discApi3.year,
            addBy = AddBy.MANUALLY
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_3",
            "title_normalize_3",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_search_result)).check(matches(not(isDisplayed())))

        val result = context.resources.getQuantityString(
            R.plurals.plural_total_api_result,
            1,
            1
        )
        onView(withId(R.id.tv_disc_search_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.layout_disc_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_disc_add_name)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.name)))
        }

        onView(withId(R.id.tv_disc_add_title)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.title)))
        }

        onView(withId(R.id.tv_disc_add_year)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discAdd.year)))
        }

        val nb = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_manually,
            1,
            1
        )
        onView(withId(R.id.tv_disc_present_manually)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nb)))
        }
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        val chooseDisc = context.getString(R.string.choose_disc)
        onView(withId(R.id.tv_choose_disc)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(chooseDisc)))
        }

        onView(withId(R.id.rv_list_disc_search))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                matches(isDisplayed())

                val recyclerView = view as RecyclerView
                val adapterItemCount = recyclerView.adapter?.itemCount!!

                assertEquals(1, adapterItemCount)
            }

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun onButtonBackClicked_addDisc_popBackStack() = runTest {
        // From addDiscFragment
        discsRepository.setDiscApi(listOf(discApi1))

        val discAdd = DiscAdd(
            name = discApi1.name,
            title = discApi1.title,
            year = discApi1.year,
            addBy = AddBy.SEARCH
        )

        // Empty
        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.navigate(R.id.addDiscFragment)
            navController.navigate(R.id.discResultSearchFragment)
        }

        onView(withId(R.id.button_back)).perform(click())

        assertEquals(navController.currentDestination!!.id, R.id.addDiscFragment)
    }

    @Test
    fun onButtonBackClicked_discPresent_popBackStack() = runTest {
        // From DiscPresentFragment
        discsRepository.setDiscApi(listOf(discApi1))
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discAdd = DiscAdd(
            name = "name_1",
            title = "title_1",
            year = "year_1",
            addBy = AddBy.SEARCH
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_1",
            "title_normalize_1",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.navigate(R.id.addDiscFragment)
            navController.popBackStack(R.id.discPresentFragment, false)
            navController.navigate(R.id.discResultSearchFragment)
        }

        onView(withId(R.id.button_back)).perform(click())

        assertEquals(navController.currentDestination!!.id, R.id.addDiscFragment)
    }

    @Test
    fun onButtonBackClicked_discPresentDetail_popBackStack() = runTest {
        // From DiscPresentDetailFragment
        discsRepository.setDiscApi(listOf(discApi3))
        discsRepository.setDatabaseDisc(listOf(databaseDisc3Man))

        val discAdd = DiscAdd(
            name = discApi3.name,
            title = discApi3.title,
            year = discApi3.year,
            addBy = AddBy.MANUALLY
        )

        val listDb = discsRepository.getListDiscDbPresent(
            "name_normalize_3",
            "title_normalize_3",
            discAdd.year
        )

        val discPresent = DiscPresent(
            list = listDb,
            discAdd = discAdd
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultSearchFragmentArgs(discPresent).toBundle()
        val scenario = launchFragmentInContainer<DiscResultSearchFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.navigate(R.id.addDiscFragment)
            navController.popBackStack(R.id.discPresentDetailFragment, false)
            navController.navigate(R.id.discResultSearchFragment)
        }

        onView(withId(R.id.button_back)).perform(click())

        assertEquals(navController.currentDestination!!.id, R.id.addDiscFragment)
    }
}





