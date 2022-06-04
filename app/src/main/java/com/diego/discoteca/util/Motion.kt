package com.diego.discoteca.util

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.diego.discoteca.R
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis

fun Fragment.materialSharedAxisEnterReturnTransition(axis: Int) {
    enterTransition = MaterialSharedAxis(axis, true).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
    returnTransition = MaterialSharedAxis(axis, false).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
}

fun Fragment.materialSharedAxisExitReenterTransition(axis: Int) {
    exitTransition = MaterialSharedAxis(axis, true).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
    reenterTransition = MaterialSharedAxis(axis, false).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
}

fun Fragment.materialElevationScaleExitReenterTransition() {
    exitTransition = MaterialElevationScale(false).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
    reenterTransition = MaterialElevationScale(true).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
    }
}

fun delayedTransition(sceneRoot: ViewGroup, transition: Transition) {
    TransitionManager.beginDelayedTransition(
        sceneRoot,
        transition
    )
}