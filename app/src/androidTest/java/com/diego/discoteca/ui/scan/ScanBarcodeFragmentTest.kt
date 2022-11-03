package com.diego.discoteca.ui.scan

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diego.discoteca.R
import com.diego.discoteca.util.Destination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ScanBarcodeFragmentTest {

    @Test
    fun init_displayedInUi() {
        val bundle = ScanBarcodeFragmentArgs(Destination.API).toBundle()
        launchFragmentInContainer<ScanBarcodeFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Discoteca
        )

        onView(withId(R.id.preview_view_fragment_scan_barcode)).check(matches(isDisplayed()))
        onView(withId(R.id.img_status)).check(matches(isDisplayed()))
        onView(withId(R.id.line_status)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progress_bar_fragment_scan_barcode)).check(matches(isDisplayed()))
    }
}