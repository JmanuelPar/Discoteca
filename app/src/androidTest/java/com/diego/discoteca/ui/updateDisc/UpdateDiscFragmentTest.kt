package com.diego.discoteca.ui.updateDisc

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
import com.diego.discoteca.clickIcon
import com.diego.discoteca.database.DatabaseDisc
import com.diego.discoteca.hasTextInputLayoutHelperText
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UpdateDiscFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private val discDatabase = DatabaseDisc(
        id = 1L,
        name = "name_artist",
        title = "title_media",
        year = "2022",
        country = "",
        format = "",
        formatMedia = "",
        coverImage = "",
        barcode = "",
        idDisc = -1,
        addBy = AddBy.MANUALLY,
        nameNormalize = "name_artist_normalize",
        titleNormalize = "title_media_normalize"
    )

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
        discsRepository.setDatabaseDisc(listOf(discDatabase))

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun disc_displayInUi() {
        val bundle = UpdateDiscFragmentArgs(discDatabase.id).toBundle()
        launchFragmentInContainer<UpdateDiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.et_disc_artist)).check(matches(withText(discDatabase.name)))
        onView(withId(R.id.it_disc_artist)).check(
            matches(
                hasTextInputLayoutHelperText(discDatabase.name)
            )
        )

        onView(withId(R.id.et_disc_title)).check(matches(withText(discDatabase.title)))
        onView(withId(R.id.it_disc_title)).check(
            matches(
                hasTextInputLayoutHelperText(
                    discDatabase.title
                )
            )
        )

        onView(withId(R.id.et_disc_year)).check(matches(withText(discDatabase.year)))
        onView(withId(R.id.it_disc_year)).check(
            matches(
                hasTextInputLayoutHelperText(
                    discDatabase.year
                )
            )
        )

        val modify = context.getString(R.string.modify)
        onView(withId(R.id.button_action)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(modify)))
        }
    }

    @Test
    fun dialog_artist_displayInUi() {
        val bundle = UpdateDiscFragmentArgs(discDatabase.id).toBundle()
        launchFragmentInContainer<UpdateDiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.it_disc_artist)).perform(clickIcon(true))

        val artistName = context.getString(R.string.artist_group_name)
        val artistMsg = context.getString(R.string.information_artist_message)
        val ok = context.getString(R.string.ok)

        sleep(1000)
        onView(withText(artistName)).check(matches(isDisplayed()))
        onView(withText(artistMsg)).check(matches(isDisplayed()))
        onView(withText(ok)).check(matches(isDisplayed()))
    }

    @Test
    fun dialog_year_displayInUi() {
        val bundle = UpdateDiscFragmentArgs(discDatabase.id).toBundle()
        launchFragmentInContainer<UpdateDiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.it_disc_year)).perform(clickIcon(true))

        val yearRelease = context.getString(R.string.disc_year_release)
        val yearMsg = context.getString(R.string.information_year_message)
        val ok = context.getString(R.string.ok)

        sleep(1000)
        onView(withText(yearRelease)).check(matches(isDisplayed()))
        onView(withText(yearMsg)).check(matches(isDisplayed()))
        onView(withText(ok)).check(matches(isDisplayed()))
    }

    @Test
    fun update_disc_nav() {
        val navController = TestNavHostController(context)
        val bundle = UpdateDiscFragmentArgs(discDatabase.id).toBundle()
        val scenario = launchFragmentInContainer<UpdateDiscFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.updateDiscFragment, bundle)
        }

        onView(withId(R.id.et_disc_artist)).perform(replaceText("name_artist_update"))
        onView(withId(R.id.et_disc_title)).perform(replaceText("title_media_update"))

        // Button modify
        onView(withId(R.id.button_action)).perform(click())

        sleep(1000)
        onView(withId(R.id.button_yes)).perform(click())

        onView(isRoot()).perform(closeSoftKeyboard())

        assertEquals(navController.currentDestination?.id, R.id.discFragment)
    }
}
