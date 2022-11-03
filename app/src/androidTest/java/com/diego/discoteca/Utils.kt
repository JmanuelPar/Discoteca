package com.diego.discoteca

import android.content.Context
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.database.DatabaseDisc
import com.google.android.material.R
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun hasTextInputLayoutHelperText(expectedHelperText: String): Matcher<View> =
    object : TypeSafeMatcher<View>() {

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val helper = item.helperText ?: return false
            val helperString = helper.toString()
            return expectedHelperText == helperString
        }

        override fun describeTo(description: org.hamcrest.Description?) {
        }
    }

fun hasTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> =
    object : TypeSafeMatcher<View>() {

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val error = item.error ?: return false
            val errorString = error.toString()
            return expectedErrorText == errorString
        }

        override fun describeTo(description: org.hamcrest.Description?) {
        }
    }

fun hasTextInputLayoutHintText(expectedHintText: String): Matcher<View> =
    object : TypeSafeMatcher<View>() {

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val hint = item.hint ?: return false
            val hintString = hint.toString()
            return expectedHintText == hintString
        }

        override fun describeTo(description: org.hamcrest.Description?) {
        }
    }

fun clickIcon(isEndIcon: Boolean): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(TextInputLayout::class.java)
        }

        override fun getDescription(): String {
            return "Clicks the end or start icon"
        }

        override fun perform(uiController: UiController?, view: View) {
            val item = view as TextInputLayout
            val iconView: CheckableImageButton = item.findViewById(
                if (isEndIcon) R.id.text_input_end_icon
                else R.id.text_input_start_icon
            )
            iconView.performClick()
        }
    }
}

fun Context.getCountryYear(databaseDisc: DatabaseDisc) =
    when {
        databaseDisc.country.isEmpty() && databaseDisc.year.isEmpty() -> this.getString(com.diego.discoteca.R.string.not_specified)
        databaseDisc.country.isEmpty() && databaseDisc.year.isNotEmpty() -> databaseDisc.year
        databaseDisc.country.isNotEmpty() && databaseDisc.year.isEmpty() ->
            "${databaseDisc.country} - ${this.getString(com.diego.discoteca.R.string.not_specified)}"
        else -> "${databaseDisc.country} - ${databaseDisc.year}"
    }

fun Context.getDiscFormat(databaseDisc: DatabaseDisc) =
    databaseDisc.format.ifEmpty { this.getString(com.diego.discoteca.R.string.media_undefined) }

fun Context.getCountryYear(disc: Disc) =
    when {
        disc.country.isEmpty() && disc.year.isEmpty() -> this.getString(com.diego.discoteca.R.string.not_specified)
        disc.country.isEmpty() && disc.year.isNotEmpty() -> disc.year
        disc.country.isNotEmpty() && disc.year.isEmpty() ->
            "${disc.country} - ${this.getString(com.diego.discoteca.R.string.not_specified)}"
        else -> "${disc.country} - ${disc.year}"
    }

fun Context.getDiscLightCountryYear(discLight: DiscLight) =
    when {
        discLight.country.isEmpty() && discLight.year.isEmpty() -> this.getString(com.diego.discoteca.R.string.not_specified)
        discLight.country.isEmpty() && discLight.year.isNotEmpty() -> discLight.year
        discLight.country.isNotEmpty() && discLight.year.isEmpty() ->
            "${discLight.country} - ${this.getString(com.diego.discoteca.R.string.not_specified)}"
        else -> "${discLight.country} - ${discLight.year}"
    }

fun Context.getDiscFormat(disc: Disc) =
    disc.format.ifEmpty { this.getString(com.diego.discoteca.R.string.media_undefined) }

fun Context.getDiscLightFormat(disc: Disc) =
    disc.discLight?.format?.ifEmpty { this.getString(com.diego.discoteca.R.string.media_undefined) }