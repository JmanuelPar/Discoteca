package com.diego.discoteca.ui.interaction

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
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.getMyUIText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InteractionFragmentTest {

    private val context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    private lateinit var discsRepository: FakeAndroidDiscsRepository
    private lateinit var discDatabaseManually: DatabaseDisc
    private lateinit var discDatabaseScan: DatabaseDisc
    private lateinit var discDatabaseSearch: DatabaseDisc
    private lateinit var list: List<DatabaseDisc>

    @Before
    fun setUp() {
        discsRepository = FakeAndroidDiscsRepository()
        val databaseDiscFactory = DatabaseDiscFactory()
        discDatabaseManually = databaseDiscFactory.createDatabaseDisc(AddBy.MANUALLY)
        discDatabaseScan = databaseDiscFactory.createDatabaseDisc(AddBy.SCAN)
        discDatabaseSearch = databaseDiscFactory.createDatabaseDisc(AddBy.SEARCH)
        list = listOf(
            discDatabaseManually,
            discDatabaseScan,
            discDatabaseSearch
        )
        discsRepository.setDatabaseDisc(list)

        ServiceLocator.discsRepository = discsRepository
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun no_signIn_displayInUi() {
        launchFragmentInContainer<InteractionFragment>(
            themeResId = R.style.Theme_Discoteca
        )

        val number = context.resources.getQuantityString(
            R.plurals.plural_nb_disc_discoteca,
            list.size,
            list.size
        )
        onView(withId(R.id.discs_interaction_message)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(number)))
        }

        onView(withId(R.id.important_interaction_message)).check(matches(isDisplayed()))

        val logIn = context.getMyUIText(UIText.AccountNotLogIn)
        onView(withId(R.id.tv_log_in)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(logIn)))
        }

        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()))
        onView(withId(R.id.drive_sign_out_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.drive_disconnect_button)).check(matches(not(isDisplayed())))

        val backUp = context.getString(R.string.back_up)
        onView(withId(R.id.upload_discoteca)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(backUp)))
        }

        onView(withId(R.id.line_upload)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_drive_upload)).check(matches(isDisplayed()))
        onView(withId(R.id.progress_bar_linear_upload)).check(matches(not(isDisplayed())))

        val lastBackUp = context.getString(R.string.last_back_up)
        onView(withId(R.id.text_last_back_up)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(lastBackUp)))
        }

        val uploadTime = context.getString(R.string.please_sign_in)
        onView(withId(R.id.upload_time)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(uploadTime)))
        }

        val restore = context.getString(R.string.restore)
        onView(withId(R.id.download_discoteca)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(restore)))
        }

        onView(withId(R.id.line_download)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_drive_download)).check(matches(isDisplayed()))
        onView(withId(R.id.progress_bar_linear_download)).check(matches(not(isDisplayed())))

        val lastRestoration = context.getString(R.string.last_restore)
        onView(withId(R.id.text_last_restoration)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(lastRestoration)))
        }

        val downloadTime = context.getString(R.string.please_sign_in)
        onView(withId(R.id.download_time)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(downloadTime)))
        }
    }
}