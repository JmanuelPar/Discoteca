package com.diego.discoteca.util

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.diego.discoteca.R
import com.diego.discoteca.R.color
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscLight
import com.diego.discoteca.model.DiscResultDetail
import com.diego.discoteca.model.DiscResultScan
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("discYearEditText")
fun TextView.setDiscYearEditText(item: Disc?) {
    item?.let {
        text = item.year
    }
}

@BindingAdapter("discNameTextHelper")
fun TextInputLayout.setDiscNameTextHelper(item: Disc?) {
    item?.let {
        helperText = item.name
    }
}

@BindingAdapter("discTitleTextHelper")
fun TextInputLayout.setDiscTitleTextHelper(item: Disc?) {
    item?.let {
        helperText = item.title
    }
}

@BindingAdapter("discYearTextHelper")
fun TextInputLayout.setDiscYearTextHelper(item: Disc?) {
    item?.let {
        helperText = item.year
    }
}

@BindingAdapter("errorText")
fun TextInputLayout.setErrorText(message: String?) {
    message?.let {
        error = message
    }
}

@BindingAdapter("statusIcon")
fun ImageView.setStatusIcon(item: Boolean) {
    setColorFilter(
        if (item) MyApp.res.getColor(color.teal_200, null)
        else MyApp.res.getColor(color.black, null)
    )
}

@BindingAdapter("numberDiscs", "stateIcon")
fun ImageView.setStateIconGDriveUpload(numberDiscs: Int, stateIcon: Boolean) {
    isEnabled = when {
        stateIcon && numberDiscs > 0 -> {
            setColorFilter(
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorPrimary
                )
            )
            true
        }
        else -> {
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    color.color_on_surface_emphasis_disabled
                ),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            false
        }
    }
}

@BindingAdapter("stateIcon")
fun ImageView.setStateIconGDriveDownload(enabled: Boolean) {
    isEnabled = when {
        enabled -> {
            setColorFilter(
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorPrimary
                )
            )
            true
        }
        else -> {
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    color.color_on_surface_emphasis_disabled
                ),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            false
        }
    }
}

@BindingAdapter("numberDiscs", "isSignIn")
fun TextView.setTextInterMessage(numberDiscs: Int, isSignIn: Boolean) {
    text = when {
        (numberDiscs > 0 && isSignIn) || (numberDiscs > 0 && !isSignIn) ->
            MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_discoteca,
                numberDiscs,
                numberDiscs
            )
        numberDiscs == 0 && isSignIn ->
            MyApp.res.getString(R.string.disc_interaction_message_empty_unable_to_restore)
        else -> MyApp.res.getString(R.string.no_disc)
    }
}

@BindingAdapter("gDriveTime", "isSignIn")
fun TextView.setGDriveTime(time: String, isSignIn: Boolean) {
    text = when {
        time.isEmpty() -> if (isSignIn) "--" else context.getString(R.string.please_sign_in)
        else -> time
    }
}

@BindingAdapter("discArtistTitle")
fun TextView.setDiscArtistTitle(disc: Disc?) {
    disc?.let {
        val artistTitle = "${disc.name} - ${disc.title}"
        text = artistTitle
    }
}

@BindingAdapter("discCountryYear")
fun TextView.setDiscCountryYear(disc: Disc?) {
    disc?.let {
        text = when {
            disc.country.isEmpty() && disc.year.isEmpty() -> MyApp.res.getString(R.string.not_specified)
            disc.country.isEmpty() && disc.year.isNotEmpty() -> disc.year
            disc.country.isNotEmpty() && disc.year.isEmpty() ->
                "${disc.country} - ${MyApp.res.getString(R.string.not_specified)}"
            else -> "${disc.country} - ${disc.year}"
        }
    }
}


@BindingAdapter("discCoverImage")
fun ImageView.setCoverImage(item: Disc?) {
    item?.let {
        val imgUri = item.coverImage.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
            .load(imgUri)
            .apply(
                RequestOptions().placeholder(R.drawable.loading_animation)
                    .error(
                        ContextCompat.getDrawable(
                            this.context,
                            R.drawable.ic_disc_outline_error
                        )
                    )
                    .circleCrop()
            )
            .into(this)
    }
}

@BindingAdapter("discName")
fun TextView.setDiscName(item: Disc?) {
    item?.let { text = item.name }
}

@BindingAdapter("discTitle")
fun TextView.setDiscTitle(item: Disc?) {
    item?.let { text = item.title }
}

@BindingAdapter("discFormatMedia")
fun TextView.setDiscFormatMedia(item: Disc?) {
    item?.let {
        text = item.formatMedia.ifEmpty { MyApp.res.getString(R.string.media_undefined) }
    }
}

@BindingAdapter("discDetailFormatMedia")
fun TextView.setDiscDetailFormatMedia(item: Disc?) {
    item?.let {
        when {
            item.formatMedia.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = item.formatMedia
            }
        }
    }
}

@BindingAdapter("countFormatMedia")
fun TextView.setCountFormatMedia(formatMedia: String) {
    text = formatMedia.ifEmpty { MyApp.res.getString(R.string.undefined) }
}


@BindingAdapter("discFormat")
fun TextView.setDiscFormat(item: Disc?) {
    item?.let {
        text = item.format.ifEmpty { MyApp.res.getString(R.string.media_undefined) }
    }
}

@BindingAdapter("discBarcode")
fun TextView.setDiscBarcode(item: Disc?) {
    item?.let {
        when {
            item.barcode.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = item.barcode
            }
        }
    }
}

@BindingAdapter("totalResult")
fun TextView.setTotalResult(total: Int) {
    when (total) {
        0 -> visibility = View.GONE
        else -> {
            text = MyApp.res.getQuantityString(
                R.plurals.plural_total_result,
                total,
                total
            )
            visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("chooseDisc")
fun TextView.setChooseDisc(total: Int) {
    when (total) {
        0 -> visibility = View.GONE
        else -> {
            text = MyApp.res.getString(R.string.choose_disc)
            visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("nBManually")
fun TextView.setNbManually(size: Int) {
    when (size) {
        0 -> visibility = View.GONE
        else -> {
            visibility = View.VISIBLE
            text = MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_present_manually,
                size,
                size
            )
        }
    }
}

@BindingAdapter("nBScan")
fun TextView.setNbScan(size: Int) {
    when (size) {
        0 -> visibility = View.GONE
        else -> {
            visibility = View.VISIBLE
            text = MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_present_scan,
                size,
                size
            )
        }
    }
}

@BindingAdapter("nBSearch")
fun TextView.setNbSearch(size: Int) {
    when (size) {
        0 -> visibility = View.GONE
        else -> {
            visibility = View.VISIBLE
            text = MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_present_search,
                size,
                size
            )
        }
    }
}

@BindingAdapter("nBDiscPresentDiscoteca")
fun TextView.setNbDiscPresentDiscoteca(size: Int) {
    when (size) {
        0 -> visibility = View.GONE
        else -> {
            visibility = View.VISIBLE
            text = MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_present_discoteca,
                size,
                size
            )
        }
    }
}

@BindingAdapter("discPresent")
fun TextView.setDiscPresent(item: Disc?) {
    item?.let { disc ->
        when {
            disc.isPresentByManually == true -> {
                visibility = View.VISIBLE
                text = MyApp.res.getString(R.string.disc_present_manually)
            }
            disc.isPresentByScan == true -> {
                visibility = View.VISIBLE
                text = MyApp.res.getString(R.string.disc_present_scan)
            }
            disc.isPresentBySearch == true -> {
                visibility = View.VISIBLE
                text = MyApp.res.getString(R.string.disc_present_search)
            }
            else -> visibility = View.GONE
        }
    }
}

@BindingAdapter("strokeColorPresent")
fun MaterialCardView.setStrokeColorPresent(item: Disc?) {
    item?.let {
        strokeColor = when {
            item.isPresentByScan == true -> {
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorPrimary
                )
            }
            item.isPresentBySearch == true -> {
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorPrimaryVariant
                )
            }
            else -> ContextCompat.getColor(context, color.color_stroke_card)
        }
    }
}

@BindingAdapter("discLightName")
fun TextView.setDiscLightName(item: DiscLight?) {
    item?.let { text = item.name }
}

@BindingAdapter("discLightTitle")
fun TextView.setDiscLightTitle(item: DiscLight?) {
    item?.let { text = item.title }
}

@BindingAdapter("discLightCountryYear")
fun TextView.setDiscLightCountryYear(item: DiscLight?) {
    item?.let {
        text = when {
            item.country.isEmpty() && item.year.isEmpty() -> MyApp.res.getString(R.string.not_specified)
            item.country.isEmpty() && item.year.isNotEmpty() -> item.year
            item.country.isNotEmpty() && item.year.isEmpty() ->
                "${item.country} - ${MyApp.res.getString(R.string.not_specified)}"
            else -> "${item.country} - ${item.year}"
        }
    }
}

@BindingAdapter("discLightFormatMedia")
fun TextView.setDiscLightFormatMedia(item: DiscLight?) {
    item?.let {
        when {
            item.formatMedia.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = item.formatMedia
            }
        }
    }
}

@BindingAdapter("discLightFormat")
fun TextView.setDiscLightFormat(item: Disc?) {
    item?.let { disc ->
        when (disc.isPresentByManually) {
            true -> visibility = View.GONE
            else -> {
                text =
                    disc.discLight?.format?.ifEmpty { MyApp.res.getString(R.string.media_undefined) }
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("discLightVisibility")
fun LinearLayout.setDiscLightVisibility(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        visibility = when (discResultDetail.code) {
            SCAN ->
                if (discResultDetail.disc.isPresentByManually == true
                    || discResultDetail.disc.isPresentBySearch == true
                ) View.VISIBLE else View.GONE
            else ->
                if (discResultDetail.disc.isPresentByManually == true) View.VISIBLE
                else View.GONE
        }
    }
}

@BindingAdapter("discResultDetailAnswer")
fun TextView.setDiscResultDetailAnswer(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when (discResultDetail.code) {
            SCAN -> {
                when {
                    discResultDetail.disc.isPresentByScan == true -> {
                        visibility = View.GONE
                    }
                    discResultDetail.disc.isPresentByManually == true
                            || discResultDetail.disc.isPresentBySearch == true -> {
                        text = MyApp.res.getString(R.string.answer_update)
                        visibility = View.VISIBLE
                    }
                    else -> {
                        text = MyApp.res.getString(R.string.answer_add)
                        visibility = View.VISIBLE
                    }
                }
            }
            SEARCH -> {
                when {
                    discResultDetail.disc.isPresentByScan == true
                            || discResultDetail.disc.isPresentBySearch == true -> {
                        visibility = View.GONE
                    }
                    discResultDetail.disc.isPresentByManually == true -> {
                        text = MyApp.res.getString(R.string.answer_update)
                        visibility = View.VISIBLE
                    }
                    else -> {
                        text = MyApp.res.getString(R.string.answer_add)
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}

@BindingAdapter("buttonYesResultDetail")
fun MaterialButton.setButtonYesResultDetail(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        text = when (discResultDetail.code) {
            SCAN -> {
                when {
                    discResultDetail.disc.isPresentByScan == true -> MyApp.res.getString(R.string.ok)
                    discResultDetail.disc.isPresentByManually == true
                            || discResultDetail.disc.isPresentBySearch == true ->
                        MyApp.res.getString(R.string.update)
                    else -> MyApp.res.getString(R.string.add)
                }
            }
            // SEARCH
            else -> {
                when {
                    discResultDetail.disc.isPresentByScan == true
                            || discResultDetail.disc.isPresentBySearch == true -> MyApp.res.getString(
                        R.string.ok
                    )
                    discResultDetail.disc.isPresentByManually == true -> MyApp.res.getString(R.string.update)
                    else -> MyApp.res.getString(R.string.add)
                }
            }
        }
    }
}

@BindingAdapter("buttonNoResultDetail")
fun MaterialButton.setButtonNoResultDetail(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when (discResultDetail.code) {
            SCAN -> {
                if (discResultDetail.disc.isPresentByScan == true) {
                    visibility = View.GONE
                } else {
                    text = MyApp.res.getString(R.string.cancel)
                    visibility = View.VISIBLE
                }
            }
            else -> {
                if (discResultDetail.disc.isPresentByScan == true
                    || discResultDetail.disc.isPresentBySearch == true
                ) {
                    visibility = View.GONE
                } else {
                    text = MyApp.res.getString(R.string.cancel)
                    visibility = View.VISIBLE
                }
            }
        }
    }
}

@BindingAdapter("messageNoResultRecommendation")
fun TextView.setMessageNoResultRecommendation(item: DiscResultScan?) {
    item?.let { discScan ->
        text = when (discScan.code) {
            API -> MyApp.res.getString(R.string.disc_recommendation_api)
            else -> MyApp.res.getString(R.string.disc_recommendation_database)
        }
    }
}

@BindingAdapter("discAddText")
fun TextView.setDiscAddText(code: Int) {
    text = when (code) {
        MANUALLY -> MyApp.res.getString(R.string.disc_add_manually)
        SEARCH -> MyApp.res.getString(R.string.disc_add_search)
        else -> MyApp.res.getString(R.string.disc_add_scan)
    }
}

@BindingAdapter("isVisible")
fun TextView.setIsVisible(isVisible: Boolean) {
    when {
        isVisible -> {
            text = MyApp.res.getString(R.string.answer_search)
            visibility = View.VISIBLE
        }
        else -> visibility = View.GONE
    }
}

@BindingAdapter("buttonSearch")
fun MaterialButton.setButtonSearch(isVisible: Boolean) {
    when {
        isVisible -> {
            text = MyApp.res.getString(R.string.search)
            visibility = View.VISIBLE
        }
        else -> visibility = View.GONE
    }
}

@BindingAdapter("buttonCancelOk")
fun MaterialButton.setButtonCancelOk(isVisible: Boolean) {
    text = when {
        isVisible -> MyApp.res.getString(R.string.cancel)
        else -> MyApp.res.getString(R.string.ok)
    }
}

@BindingAdapter("textPresent")
fun TextView.setTextPresent(code: Int) {
    text = when (code) {
        MANUALLY -> MyApp.res.getString(R.string.answer_add_still)
        else -> MyApp.res.getString(R.string.answer_search_still)
    }
}

@BindingAdapter("buttonAddPresent")
fun MaterialButton.setButtonAddPresent(addBy: Int) {
    text = when (addBy) {
        MANUALLY -> MyApp.res.getString(R.string.add)
        else -> MyApp.res.getString(R.string.search)
    }
}

@BindingAdapter("countDiscs")
fun TextView.setCountDiscs(count: Int) {
    text = when (count) {
        0 -> MyApp.res.getString(R.string.no_disc)
        else -> MyApp.res.getQuantityString(
            R.plurals.plural_nb_disc_discoteca,
            count,
            count
        )
    }
}

@BindingAdapter("tvBarcode")
fun TextView.setTvBarcode(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when {
            discResultDetail.code == SEARCH
                    && (discResultDetail.disc.isPresentBySearch == true
                    || discResultDetail.disc.barcode.isEmpty()) ->
                visibility = View.GONE
            else -> {
                text = MyApp.res.getString(R.string.barcode)
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("discBarcodeDetail")
fun TextView.setDiscBarcodeDetail(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when {
            discResultDetail.code == SEARCH
                    && (discResultDetail.disc.isPresentBySearch == true
                    || discResultDetail.disc.barcode.isEmpty()) ->
                visibility = View.GONE
            else -> {
                text = item.disc.barcode
                visibility = View.VISIBLE
            }
        }
    }
}


