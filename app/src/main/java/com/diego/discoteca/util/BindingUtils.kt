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
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.data.model.DiscResultScan
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("discYearEditText")
fun TextView.setDiscYearEditText(item: Disc?) {
    item?.let { disc -> text = disc.year }
}

@BindingAdapter("discNameTextHelper")
fun TextInputLayout.setDiscNameTextHelper(item: Disc?) {
    item?.let { disc -> helperText = disc.name }
}

@BindingAdapter("discTitleTextHelper")
fun TextInputLayout.setDiscTitleTextHelper(item: Disc?) {
    item?.let { disc -> helperText = disc.title }
}

@BindingAdapter("discYearTextHelper")
fun TextInputLayout.setDiscYearTextHelper(item: Disc?) {
    item?.let { disc -> helperText = disc.year }
}

@BindingAdapter("errorText")
fun TextInputLayout.setErrorText(uiText: UIText?) {
    isErrorEnabled = uiText?.let {
        error = context.getMyUIText(it)
        true
    } ?: false
}

@BindingAdapter("statusIcon")
fun ImageView.setStatusIcon(item: Boolean) {
    setColorFilter(
        ContextCompat.getColor(context, if (item) color.teal_200 else color.black),
        android.graphics.PorterDuff.Mode.SRC_IN
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
            context.resources.getQuantityString(
                R.plurals.plural_nb_disc_discoteca,
                numberDiscs,
                numberDiscs
            )
        numberDiscs == 0 && isSignIn ->
            context.getString(R.string.disc_interaction_message_empty_unable_to_restore)
        else -> context.getString(R.string.no_disc)
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
fun TextView.setDiscArtistTitle(item: Disc?) {
    item?.let { disc ->
        val artistTitle = "${disc.name} - ${disc.title}"
        text = artistTitle
    }
}

@BindingAdapter("discCountryYear")
fun TextView.setDiscCountryYear(item: Disc?) {
    item?.let { disc ->
        text = when {
            disc.country.isEmpty() && disc.year.isEmpty() -> context.getString(R.string.not_specified)
            disc.country.isEmpty() && disc.year.isNotEmpty() -> disc.year
            disc.country.isNotEmpty() && disc.year.isEmpty() ->
                "${disc.country} - ${context.getString(R.string.not_specified)}"
            else -> "${disc.country} - ${disc.year}"
        }
    }
}

@BindingAdapter("discCoverImage")
fun ImageView.setCoverImage(item: Disc?) {
    item?.let { disc ->
        val imgUri = disc.coverImage.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions().placeholder(R.drawable.loading_animation)
                    .error(
                        ContextCompat.getDrawable(
                            context,
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
    item?.let { disc -> text = disc.name }
}

@BindingAdapter("discTitle")
fun TextView.setDiscTitle(item: Disc?) {
    item?.let { disc -> text = disc.title }
}

@BindingAdapter("discFormatMedia")
fun TextView.setDiscFormatMedia(item: Disc?) {
    item?.let { disc ->
        text = disc.formatMedia.ifEmpty { context.getString(R.string.media_undefined) }
    }
}

@BindingAdapter("discDetailFormatMedia")
fun TextView.setDiscDetailFormatMedia(item: Disc?) {
    item?.let { disc ->
        when {
            disc.formatMedia.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = disc.formatMedia
            }
        }
    }
}

@BindingAdapter("countFormatMedia")
fun TextView.setCountFormatMedia(formatMedia: String) {
    text = formatMedia.ifEmpty { context.getString(R.string.undefined) }
}


@BindingAdapter("discFormat")
fun TextView.setDiscFormat(item: Disc?) {
    item?.let { disc ->
        text = disc.format.ifEmpty { context.getString(R.string.media_undefined) }
    }
}

@BindingAdapter("discBarcode")
fun TextView.setDiscBarcode(item: Disc?) {
    item?.let { disc ->
        when {
            disc.barcode.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = disc.barcode
            }
        }
    }
}

@BindingAdapter("totalResult")
fun TextView.setTotalResult(total: Int) {
    when (total) {
        0 -> visibility = View.GONE
        else -> {
            text = context.resources.getQuantityString(
                R.plurals.plural_total_api_result,
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
            text = context.getString(R.string.choose_disc)
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
            text = context.resources.getQuantityString(
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
            text = context.resources.getQuantityString(
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
            text = context.resources.getQuantityString(
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
            text = context.resources.getQuantityString(
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
                text = context.getString(R.string.disc_present_manually)
            }
            disc.isPresentByScan == true -> {
                visibility = View.VISIBLE
                text = context.getString(R.string.disc_present_scan)
            }
            disc.isPresentBySearch == true -> {
                visibility = View.VISIBLE
                text = context.getString(R.string.disc_present_search)
            }
            else -> visibility = View.GONE
        }
    }
}

@BindingAdapter("strokeColorPresent")
fun MaterialCardView.setStrokeColorPresent(item: Disc?) {
    item?.let { disc ->
        strokeColor = when {
            disc.isPresentByScan == true -> {
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorPrimary
                )
            }
            disc.isPresentBySearch == true -> {
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
    item?.let { discLight -> text = discLight.name }
}

@BindingAdapter("discLightTitle")
fun TextView.setDiscLightTitle(item: DiscLight?) {
    item?.let { discLight -> text = discLight.title }
}

@BindingAdapter("discLightCountryYear")
fun TextView.setDiscLightCountryYear(item: DiscLight?) {
    item?.let { discLight ->
        text = when {
            discLight.country.isEmpty() && discLight.year.isEmpty() -> context.getString(R.string.not_specified)
            discLight.country.isEmpty() && discLight.year.isNotEmpty() -> discLight.year
            discLight.country.isNotEmpty() && discLight.year.isEmpty() ->
                "${discLight.country} - ${context.getString(R.string.not_specified)}"
            else -> "${discLight.country} - ${discLight.year}"
        }
    }
}

@BindingAdapter("discLightFormatMedia")
fun TextView.setDiscLightFormatMedia(item: DiscLight?) {
    item?.let { discLight ->
        when {
            discLight.formatMedia.isEmpty() -> visibility = View.GONE
            else -> {
                visibility = View.VISIBLE
                text = discLight.formatMedia
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
                    disc.discLight?.format?.ifEmpty { context.getString(R.string.media_undefined) }
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("discLightVisibility")
fun LinearLayout.setDiscLightVisibility(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        visibility = when (discResultDetail.addBy) {
            AddBy.SCAN ->
                if (discResultDetail.disc.isPresentByManually == true
                    || discResultDetail.disc.isPresentBySearch == true
                ) View.VISIBLE else View.GONE
            // AddBy.SEARCH
            else ->
                if (discResultDetail.disc.isPresentByManually == true) View.VISIBLE
                else View.GONE
        }
    }
}

@BindingAdapter("discResultDetailAnswer")
fun TextView.setDiscResultDetailAnswer(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when (discResultDetail.addBy) {
            AddBy.SCAN -> {
                when {
                    discResultDetail.disc.isPresentByScan == true -> {
                        visibility = View.GONE
                    }
                    discResultDetail.disc.isPresentByManually == true
                            || discResultDetail.disc.isPresentBySearch == true -> {
                        text = context.getString(R.string.answer_update)
                        visibility = View.VISIBLE
                    }
                    else -> {
                        text = context.getString(R.string.answer_add)
                        visibility = View.VISIBLE
                    }
                }
            }
            // AddBy.SEARCH
            else -> {
                when {
                    discResultDetail.disc.isPresentByScan == true
                            || discResultDetail.disc.isPresentBySearch == true -> {
                        visibility = View.GONE
                    }
                    discResultDetail.disc.isPresentByManually == true -> {
                        text = context.getString(R.string.answer_update)
                        visibility = View.VISIBLE
                    }
                    else -> {
                        text = context.getString(R.string.answer_add)
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
        text = when (discResultDetail.addBy) {
            AddBy.SCAN -> {
                when {
                    discResultDetail.disc.isPresentByScan == true -> context.getString(R.string.ok)
                    discResultDetail.disc.isPresentByManually == true
                            || discResultDetail.disc.isPresentBySearch == true ->
                        context.getString(R.string.update)
                    else -> context.getString(R.string.add)
                }
            }
            // AddBy.SEARCH
            else -> {
                when {
                    discResultDetail.disc.isPresentByScan == true
                            || discResultDetail.disc.isPresentBySearch == true -> context.getString(
                        R.string.ok
                    )
                    discResultDetail.disc.isPresentByManually == true -> context.getString(R.string.update)
                    else -> context.getString(R.string.add)
                }
            }
        }
    }
}

@BindingAdapter("buttonNoResultDetail")
fun MaterialButton.setButtonNoResultDetail(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when (discResultDetail.addBy) {
            AddBy.SCAN -> {
                if (discResultDetail.disc.isPresentByScan == true) {
                    visibility = View.GONE
                } else {
                    text = context.getString(R.string.cancel)
                    visibility = View.VISIBLE
                }
            }
            // AddBy.SEARCH
            else -> {
                if (discResultDetail.disc.isPresentByScan == true
                    || discResultDetail.disc.isPresentBySearch == true
                ) {
                    visibility = View.GONE
                } else {
                    text = context.getString(R.string.cancel)
                    visibility = View.VISIBLE
                }
            }
        }
    }
}

@BindingAdapter("messageNoResultRecommendation")
fun TextView.setMessageNoResultRecommendation(item: DiscResultScan?) {
    item?.let { discResultScan ->
        text = when (discResultScan.destination) {
            Destination.API -> context.getString(R.string.disc_recommendation_api)
            Destination.DATABASE -> context.getString(R.string.disc_recommendation_database)
            else -> context.getString(R.string.undefined)
        }
    }
}

@BindingAdapter("discAddText")
fun TextView.setDiscAddText(item: Disc?) {
    item?.let { disc ->
        text = when (disc.addBy) {
            AddBy.MANUALLY -> context.getString(R.string.disc_add_manually)
            AddBy.SEARCH -> context.getString(R.string.disc_add_search)
            // AddBy.SCAN
            else -> context.getString(R.string.disc_add_scan)
        }
    }
}

@BindingAdapter("isVisible")
fun TextView.setIsVisible(isVisible: Boolean) {
    when {
        isVisible -> {
            text = context.getString(R.string.answer_search)
            visibility = View.VISIBLE
        }
        else -> visibility = View.GONE
    }
}

@BindingAdapter("buttonSearch")
fun MaterialButton.setButtonSearch(isVisible: Boolean) {
    when {
        isVisible -> {
            text = context.getString(R.string.search)
            visibility = View.VISIBLE
        }
        else -> visibility = View.GONE
    }
}

@BindingAdapter("buttonCancelOk")
fun MaterialButton.setButtonCancelOk(isVisible: Boolean) {
    text = when {
        isVisible -> context.getString(R.string.cancel)
        else -> context.getString(R.string.ok)
    }
}

@BindingAdapter("answerPresent")
fun TextView.setAnswerPresent(item: DiscPresent?) {
    item?.let { discPresent ->
        text = when (discPresent.discAdd.addBy) {
            AddBy.MANUALLY -> context.getString(R.string.answer_add_still)
            // AddBy.SEARCH
            else -> context.getString(R.string.answer_search_still)
        }
    }
}

@BindingAdapter("buttonAddPresent")
fun MaterialButton.setButtonAddPresent(item: DiscPresent?) {
    item?.let { discPresent ->
        text = when (discPresent.discAdd.addBy) {
            AddBy.MANUALLY -> context.getString(R.string.add)
            // AddBy.SEARCH
            else -> context.getString(R.string.search)
        }
    }
}

@BindingAdapter("discAddName")
fun TextView.setDiscAddName(item: DiscPresent?) {
    item?.let { discPresent ->
        text = discPresent.discAdd.name
    }
}

@BindingAdapter("discAddTitle")
fun TextView.setDiscAddTitle(item: DiscPresent?) {
    item?.let { discPresent ->
        text = discPresent.discAdd.title
    }
}

@BindingAdapter("discAddYear")
fun TextView.setDiscAddYear(item: DiscPresent?) {
    item?.let { discPresent ->
        text = discPresent.discAdd.year
    }
}

@BindingAdapter("countDiscs")
fun TextView.setCountDiscs(count: Int) {
    text = when (count) {
        0 -> context.getString(R.string.no_disc)
        else -> context.resources.getQuantityString(
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
            discResultDetail.addBy == AddBy.SEARCH
                    && (discResultDetail.disc.isPresentBySearch == true
                    || discResultDetail.disc.barcode.isEmpty()) ->
                visibility = View.GONE
            else -> {
                text = context.getString(R.string.barcode)
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("discBarcodeDetail")
fun TextView.setDiscBarcodeDetail(item: DiscResultDetail?) {
    item?.let { discResultDetail ->
        when {
            discResultDetail.addBy == AddBy.SEARCH
                    && (discResultDetail.disc.isPresentBySearch == true
                    || discResultDetail.disc.barcode.isEmpty()) ->
                visibility = View.GONE
            else -> {
                text = discResultDetail.disc.barcode
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("getUIText")
fun TextView.getUIText(uiText: UIText?) {
    uiText?.let {
        text = context.getMyUIText(it)
    }
}