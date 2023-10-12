package com.diego.discoteca.ui.discResultScan

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
import com.diego.discoteca.*
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscResultScan
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.util.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@MediumTest
@RunWith(AndroidJUnit4::class)
class DiscResultScanFragmentTest {

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

        discsRepository.setDiscApi(
            listOf(
                discApi1, discApi2, discApi3
            )
        )
        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun scan_disc_in_api_displayInUi() {
        val discResultScan = DiscResultScan(
            barcode = discApi1.barcode, destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalApi(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_in_database_displayInUi() {
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discResultScan = DiscResultScan(
            barcode = databaseDisc1Sc.barcode, destination = Destination.DATABASE
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalDatabase(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_in_api_disc_in_database_add_scan_displayInUi() {/* barcode_1 -> databaseDisc1Sc.barcode, discApi1.barcode
           databaseDisc1Sc -> add by scan */
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discResultScan = DiscResultScan(
            barcode = discApi1.barcode, destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalApi(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val nbScan = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_scan, 1, 1
        )
        onView(withId(R.id.tv_disc_present_scan)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbScan)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_in_api_disc_in_database_add_search_displayInUi() {/* discApi2 -> name_2
           databaseDisc2Se-> name_2, add by search */
        discsRepository.setDatabaseDisc(listOf(databaseDisc2Se))

        val discResultScan = DiscResultScan(
            barcode = discApi2.barcode, destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalApi(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val nbScan = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_search, 1, 1
        )
        onView(withId(R.id.tv_disc_present_search)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbScan)))
        }

        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_in_api_disc_in_database_add_manually_displayInUi() {/* discApi3 -> name_3
           databaseDisc3Man -> name_3, add manually */
        discsRepository.setDatabaseDisc(listOf(databaseDisc3Man))

        val discResultScan = DiscResultScan(
            barcode = discApi3.barcode, destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalApi(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val nbScan = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_manually, 1, 1
        )
        onView(withId(R.id.tv_disc_present_manually)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbScan)))
        }

        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_in_database_disc_add_scan_displayInUi() {/* barcode_1 -> databaseDisc1Sc.barcode, discApi1.barcode
           databaseDisc1Sc -> add by scan */
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discResultScan = DiscResultScan(
            barcode = discApi1.barcode, destination = Destination.DATABASE
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalDatabase(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_api_not_found_displayInUi() {
        val discResultScan = DiscResultScan(
            barcode = "barcode_4", destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(isDisplayed()))

        onView(withId(R.id.img_disc)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val message = context.getString(R.string.disc_recommendation_api)
        onView(withId(R.id.tv_no_scan_result_recommendation)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(message)))
        }

        onView(withId(R.id.layout_result)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun scan_disc_in_database_disc_add_search_displayInUi() {
        discsRepository.setDatabaseDisc(listOf(databaseDisc2Se))

        val discResultScan = DiscResultScan(
            barcode = discApi2.barcode, destination = Destination.DATABASE
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(isDisplayed()))

        onView(withId(R.id.img_disc)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val message = context.getString(R.string.disc_recommendation_database)
        onView(withId(R.id.tv_no_scan_result_recommendation)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(message)))
        }

        onView(withId(R.id.layout_result)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun scan_disc_in_database_disc_add_manually_displayInUi() {
        discsRepository.setDatabaseDisc(listOf(databaseDisc3Man))

        val discResultScan = DiscResultScan(
            barcode = discApi3.barcode, destination = Destination.DATABASE
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(isDisplayed()))

        onView(withId(R.id.img_disc)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_result_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val message = context.getString(R.string.disc_recommendation_database)
        onView(withId(R.id.tv_no_scan_result_recommendation)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(message)))
        }

        onView(withId(R.id.layout_result)).check(matches(not(isDisplayed())))

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_error)).check(matches(not(isDisplayed())))
    }

    @Test
    fun scan_disc_in_database_more_displayInUi() {/* disc added by scan, disc added manually
           discApi3 -> name_3 */
        val databaseDisc3Sc = DatabaseDisc(
            id = 1L,
            name = "name_3",
            title = "title_3",
            year = "year_3",
            country = "country_3",
            format = "format_3",
            formatMedia = "format_media_3",
            coverImage = "url_coverImage_3",
            barcode = "barcode_3",
            idDisc = 2,
            addBy = AddBy.SCAN,
            nameNormalize = "name_normalize_3",
            titleNormalize = "title_normalize_3"
        )

        discsRepository.setDatabaseDisc(
            listOf(
                databaseDisc3Sc, databaseDisc3Man
            )
        )

        val discResultScan = DiscResultScan(
            barcode = discApi3.barcode, destination = Destination.DATABASE
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalDatabase(2))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        onView(withId(R.id.tv_disc_present_manually)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun scan_disc_api_more_displayInUi() {/* disc added by scan, disc added manually
           discApi3 -> name_3
           Priority to add by scan / add by search */
        val databaseDisc3Sc = DatabaseDisc(
            id = 1L,
            name = "name_3",
            title = "title_3",
            year = "year_3",
            country = "country_3",
            format = "format_3",
            formatMedia = "format_media_3",
            coverImage = "url_coverImage_3",
            barcode = "barcode_3",
            idDisc = 2,
            addBy = AddBy.SCAN,
            nameNormalize = "name_normalize_3",
            titleNormalize = "title_normalize_3"
        )

        discsRepository.setDatabaseDisc(
            listOf(
                databaseDisc3Sc, databaseDisc3Man
            )
        )

        val discResultScan = DiscResultScan(
            barcode = discApi3.barcode, destination = Destination.API
        )

        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.layout_no_scan_result)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))

        val result = context.getMyUIText(UIText.TotalApi(1))
        onView(withId(R.id.tv_result)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(result)))
        }

        onView(withId(R.id.disc_barcode)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(discResultScan.barcode)))
        }

        val nbManually = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_present_manually, 1, 1
        )
        onView(withId(R.id.tv_disc_present_manually)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(nbManually)))
        }

        onView(withId(R.id.tv_disc_present_scan)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_disc_present_search)).check(matches(not(isDisplayed())))

        onView(withId(R.id.tv_choose_disc)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_list_disc_result_scan)).check { view, noViewFoundException ->
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
    fun onDiscResultClicked_navToDiscResultDetail() {
        val discResultScan = DiscResultScan(
            barcode = discApi1.barcode, destination = Destination.API
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        val scenario = launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discResultScanFragment)
        }

        onView(withId(R.id.rv_list_disc_result_scan)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, click()
                )
            )

        assertEquals(navController.currentDestination?.id, R.id.discResultDetailFragment)
    }

    @Test
    fun onDiscResultClicked_navToDiscDetail() {
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discResultScan = DiscResultScan(
            barcode = databaseDisc1Sc.barcode, destination = Destination.DATABASE
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        val scenario = launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discResultScanFragment)
        }

        onView(withId(R.id.rv_list_disc_result_scan)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, click()
                )
            )

        assertEquals(navController.currentDestination?.id, R.id.discDetailFragment)
    }

    @Test
    fun onButtonBackClicked_navToDisc() {
        val discResultScan = DiscResultScan(
            barcode = discApi1.barcode, destination = Destination.API
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        val scenario = launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discResultScanFragment)
        }

        sleep(1000)

        onView(withId(R.id.button_back)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }

    @Test
    fun onButtonBackClicked_navToInfo() {
        discsRepository.setDatabaseDisc(listOf(databaseDisc1Sc))

        val discResultScan = DiscResultScan(
            barcode = databaseDisc1Sc.barcode, destination = Destination.DATABASE
        )

        val navController = TestNavHostController(context)
        val bundle = DiscResultScanFragmentArgs(discResultScan).toBundle()
        val scenario = launchFragmentInContainer<DiscResultScanFragment>(
            fragmentArgs = bundle, themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.discResultScanFragment)
        }

        sleep(1000)

        onView(withId(R.id.button_back)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.infoFragment)
    }
}