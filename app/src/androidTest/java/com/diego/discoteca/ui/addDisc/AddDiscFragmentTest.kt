package com.diego.discoteca.ui.addDisc

import android.content.Context
import android.os.SystemClock.sleep
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.FakeAndroidDiscsRepository
import com.diego.discoteca.R
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.hasTextInputLayoutErrorText
import com.diego.discoteca.hasTextInputLayoutHintText
import com.diego.discoteca.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddDiscFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var nameAdd: String
    private lateinit var titleAdd: String
    private lateinit var yearAdd: String

    @Before
    fun setUp() {
        nameAdd = "name_artist"
        titleAdd = "title_media"
        yearAdd = "2022"

        discsRepository = FakeAndroidDiscsRepository()
        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun init_displayedInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val hintArtist = context.getString(R.string.artist_group_name_hint)
        onView(withId(R.id.it_disc_artist)).apply {
            check(matches(isDisplayed()))
            check(matches(hasTextInputLayoutHintText(hintArtist)))
        }

        onView(withId(R.id.et_disc_artist)).check(matches(isDisplayed()))

        val hintTitle = context.getString(R.string.disc_title_hint)
        onView(withId(R.id.it_disc_title)).apply {
            check(matches(isDisplayed()))
            check(matches(hasTextInputLayoutHintText(hintTitle)))
        }

        onView(withId(R.id.et_disc_title)).check(matches(isDisplayed()))

        val hintYear = context.getString(R.string.disc_year_release_hint)
        onView(withId(R.id.it_disc_year)).apply {
            check(matches(isDisplayed()))
            check(matches(hasTextInputLayoutHintText(hintYear)))
        }

        onView(withId(R.id.et_disc_year)).check(matches(isDisplayed()))

        onView(withId(R.id.button_search)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add)).check(matches(isDisplayed()))
    }

    @Test
    fun success_add_bottomSheet_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.bottom_sheet_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_artist_name_add)).check(matches(withText(nameAdd)))
        onView(withId(R.id.tv_title_add)).check(matches(withText(titleAdd)))
        onView(withId(R.id.tv_year_add)).check(matches(withText(yearAdd)))
    }

    @Test
    fun success_search_bottomSheet_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_search)).perform(click())

        onView(withId(R.id.bottom_sheet_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_artist_name_add)).check(matches(withText(nameAdd)))
        onView(withId(R.id.tv_title_add)).check(matches(withText(titleAdd)))
        onView(withId(R.id.tv_year_add)).check(matches(withText(yearAdd)))
    }

    @Test
    fun discArtistNameIndicate_error_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val errorMessage = context.getMyUIText(UIText.DiscArtistNameIndicate)

        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.it_disc_artist)).check(matches(hasTextInputLayoutErrorText(errorMessage)))
    }

    @Test
    fun discTitleIndicate_error_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val errorMessage = context.getMyUIText(UIText.DiscTitleIndicate)

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.it_disc_title)).check(matches(hasTextInputLayoutErrorText(errorMessage)))
    }

    @Test
    fun discYearIndicate_error_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val errorMessage = context.getMyUIText(UIText.DiscYearIndicate)

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.it_disc_year)).check(matches(hasTextInputLayoutErrorText(errorMessage)))
    }

    @Test
    fun noValidDiscYear_error_displayInUi() {
        launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val errorMessage = context.getMyUIText(UIText.NoValidDiscYear)

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText("1800"))

        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.it_disc_year)).check(matches(hasTextInputLayoutErrorText(errorMessage)))
    }

    @Test
    fun navigate_to_disc() {
        val navController = TestNavHostController(context)
        val scenario = launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.addDiscFragment)
        }

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        // Button add
        onView(withId(R.id.button_add)).check(matches(isDisplayed())).perform(click())

        // BottomSheet button yes
        sleep(1000)
        onView(withId(R.id.button_yes)).check(matches(isDisplayed())).perform(click())

        onView(isRoot()).perform(closeSoftKeyboard())

        // database is empty -> we add a disc
        assertEquals(navController.currentDestination!!.id, R.id.discFragment)
    }

    @Test
    fun navigate_to_disc_result_search() {
        val navController = TestNavHostController(context)
        val scenario = launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.addDiscFragment)
        }

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        // Button search
        onView(withId(R.id.button_search)).check(matches(isDisplayed())).perform(click())

        // BottomSheet button yes
        sleep(1000)
        onView(withId(R.id.button_yes)).check(matches(isDisplayed())).perform(click())

        // database is empty -> we search a disc
        assertEquals(navController.currentDestination!!.id, R.id.discResultSearchFragment)
    }

    @Test
    fun navigate_to_disc_present() {
        val databaseDisc = DatabaseDisc(
            id = 1L,
            name = nameAdd,
            title = titleAdd,
            year = yearAdd,
            country = "",
            format = "",
            formatMedia = "",
            coverImage = "",
            barcode = "",
            idDisc = -1,
            addBy = AddBy.MANUALLY,
            nameNormalize = nameAdd,
            titleNormalize = titleAdd
        )

        discsRepository.setDatabaseDisc(listOf(databaseDisc))

        val navController = TestNavHostController(context)
        val scenario = launchFragmentInContainer<AddDiscFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.addDiscFragment)
        }

        onView(withId(R.id.et_disc_artist)).perform(typeText(nameAdd))
        onView(withId(R.id.et_disc_title)).perform(typeText(titleAdd))
        onView(withId(R.id.et_disc_year)).perform(typeText(yearAdd))

        onView(isRoot()).perform(closeSoftKeyboard())

        // Button search
        onView(withId(R.id.button_search)).check(matches(isDisplayed())).perform(click())

        // BottomSheet button yes
        sleep(1000)
        onView(withId(R.id.button_yes)).check(matches(isDisplayed())).perform(click())

        assertEquals(navController.currentDestination!!.id, R.id.discPresentFragment)
    }
}

