package com.diego.discoteca.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.fragment.app.FragmentManager
import com.diego.discoteca.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showDialogMessageAction(
    message: String,
    positiveButton: String,
    action: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setMessage(message)
        .setNegativeButton(getString(R.string.cancel), null)
        .setPositiveButton(positiveButton) { _, _ ->
            action()
        }.show()
}

fun Context.showDialogMessageOneButton(
    message: String,
    positiveButton: String,
    action: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setMessage(message)
        .setPositiveButton(positiveButton) { _, _ ->
            action()
        }.show()
}

fun Context.showDialogTitle(
    title: String,
    message: String
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(this.resources.getString(R.string.ok), null)
        .show()
}

fun Context.showDialogTitleAction(
    title: String,
    message: String,
    action: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(this.resources.getString(R.string.ok)) { _, _ ->
            action()
        }
        .show()
}

fun FragmentManager.showBottomSheetModal(
    artistName: String,
    title: String,
    year: String,
    action: () -> Unit,
) {
    val myBottomSheetModal = MyBottomSheetDialogFragment.newInstance(
        artistName,
        title,
        year
    )
    myBottomSheetModal.show(this, MyBottomSheetDialogFragment.TAG)
    myBottomSheetModal.setOnButtonsClick(object :
        MyBottomSheetDialogFragment.MyBottomSheetListener {
        override fun yesButtonClicked() {
            // One action
            action()
        }

        override fun noButtonClicked() {
            // Nothing, only dismiss
        }
    })
}

fun View.showSnackBar(message: String, anchorView: View) {
    MySnackBar.make(
        this,
        message,
        MySnackBar.LENGTH_INDEFINITE
    )
        .setAnchorView(anchorView)
        .show()
}

fun View.showSnackBarNoAnchor(message: String) {
    MySnackBar.make(
        this,
        message,
        MySnackBar.LENGTH_INDEFINITE
    )
        .show()
}

/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.GRAY)
    }
}




