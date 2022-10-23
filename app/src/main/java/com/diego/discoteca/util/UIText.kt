package com.diego.discoteca.util

import android.content.Context
import android.os.Parcelable
import com.diego.discoteca.R
import kotlinx.parcelize.Parcelize

sealed class UIText : Parcelable {
    @Parcelize
    object DiscArtistNameIndicate : UIText()

    @Parcelize
    object DiscTitleIndicate : UIText()

    @Parcelize
    object DiscYearIndicate : UIText()

    @Parcelize
    object NoValidDiscYear : UIText()

    @Parcelize
    object DiscAdded : UIText()

    @Parcelize
    object DiscDeleted : UIText()

    @Parcelize
    object DiscUpdated : UIText()

    @Parcelize
    object DiscAlreadyPresentOne : UIText()

    @Parcelize
    object DiscAlreadyPresentMore : UIText()

    @Parcelize
    object DatabaseBackUp : UIText()

    @Parcelize
    object DatabaseRestored : UIText()

    @Parcelize
    object DatabaseNotRestored : UIText()

    @Parcelize
    object AccountNotLogIn : UIText()

    @Parcelize
    object NoDisplay : UIText()

    @Parcelize
    data class TotalApi(val total: Int) : UIText()

    @Parcelize
    data class TotalDatabase(val total: Int) : UIText()

    @Parcelize
    data class AccountLogIn(val account: String) : UIText()
}

fun Context.getMyUIText(uiText: UIText): String {
    return when (uiText) {
        is UIText.DiscArtistNameIndicate -> getString(R.string.indicate_artist_group_name)
        is UIText.DiscTitleIndicate -> getString(R.string.indicate_title_disc)
        is UIText.DiscYearIndicate -> getString(R.string.indicate_year_disc)
        is UIText.NoValidDiscYear -> getString(R.string.no_valid_year)
        is UIText.DiscAdded -> getString(R.string.disc_added)
        is UIText.DiscDeleted -> getString(R.string.disc_deleted)
        is UIText.DiscUpdated -> getString(R.string.disc_updated)
        is UIText.DiscAlreadyPresentOne -> getString(R.string.disc_already_present_one)
        is UIText.DiscAlreadyPresentMore -> getString(R.string.disc_already_present_more)
        is UIText.DatabaseBackUp -> getString(R.string.database_back_up)
        is UIText.DatabaseRestored -> getString(R.string.database_restored)
        is UIText.DatabaseNotRestored -> getString(R.string.database_not_restored)
        is UIText.AccountNotLogIn -> getString(R.string.sign_in_recommendation)
        is UIText.NoDisplay -> getString(R.string.noDisplay)
        is UIText.TotalApi -> resources.getQuantityString(
            R.plurals.plural_total_api_result,
            uiText.total,
            uiText.total
        )
        is UIText.TotalDatabase -> resources.getQuantityString(
            R.plurals.plural_total_database_result,
            uiText.total,
            uiText.total
        )
        is UIText.AccountLogIn -> uiText.account
    }
}
