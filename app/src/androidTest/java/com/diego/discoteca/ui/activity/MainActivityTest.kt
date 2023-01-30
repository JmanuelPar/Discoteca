package com.diego.discoteca.ui.activity

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.diego.discoteca.R
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    private var isAirPlaneMode = false

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    @Before
    fun setup() {
        isAirPlaneMode = Settings.System.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) == 1
    }

    @Test
    fun activity_displayInUi() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.layout_no_internet)).check(matches(not(isDisplayed())))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.bottom_app_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.fab)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun noInternet_displayInUi() {
        if (!isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.layout_no_internet)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun fab_expandButton_displayInUi() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.toolbar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.bottom_app_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_card_scrim)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_card_view)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun fab_collapseButton_displayInUi() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.menu_card_scrim)).perform(click())

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.bottom_app_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_card_scrim)).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_card_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.fab)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun nav_add_disc_manually() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.manually_item)).perform(click())
        onView(withId(R.id.add_disc_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun add_disc_manually_backPress_toMainActivity() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.manually_item)).perform(click())
        onView(withId(R.id.add_disc_layout)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.main_coordinator_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun nav_scan() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.scan_item)).perform(click())
        onView(withId(R.id.scan_barcode_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun nav_scan_backPress_toMainActivity() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab)).apply {
            check(matches(isDisplayed()))
            perform(click())
        }

        onView(withId(R.id.scan_item)).perform(click())
        onView(withId(R.id.scan_barcode_layout)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.main_coordinator_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun nav_info_fragment() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.infoFragment)).perform(click())
        onView(withId(R.id.info_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun info_fragment_pressBack_toMainActivity() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.infoFragment)).perform(click())
        onView(withId(R.id.info_layout)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.main_coordinator_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun nav_interaction_fragment() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.interFragment)).perform(click())
        onView(withId(R.id.interaction_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun interaction_fragment_pressBack_toMainActivity() {
        if (isAirPlaneMode) {
            clickAirplaneMode(getInstrumentation(), context)
            sleep(4000)
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.interFragment)).perform(click())
        onView(withId(R.id.interaction_layout)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.main_coordinator_layout)).check(matches(isDisplayed()))

        scenario.close()
    }

    private fun clickAirplaneMode(instrumentation: Instrumentation, targetContext: Context) {
        UiDevice.getInstance(instrumentation).run {
            targetContext.packageManager.getLaunchIntentForPackage("com.android.settings")
                ?.let { intent ->
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    targetContext.startActivity(intent)
                    findObject(UiSelector().textContains("Network")).clickAndWaitForNewWindow()
                    findObject(UiSelector().text("Airplane mode")).click()
                }
        }
    }
}