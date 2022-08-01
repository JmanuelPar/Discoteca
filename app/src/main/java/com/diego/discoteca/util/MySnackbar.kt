package com.diego.discoteca.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.diego.discoteca.R
import com.diego.discoteca.databinding.ItemCustomSnackbarBinding
import com.google.android.material.snackbar.BaseTransientBottomBar

typealias AnimationContentViewCallback = com.google.android.material.snackbar.ContentViewCallback

class MySnackBar private constructor(
    parent: ViewGroup,
    content: View,
    callback: AnimationContentViewCallback
) : BaseTransientBottomBar<MySnackBar>(parent, content, callback) {

    init {
        getView().setPadding(0, 0, 0, 0)
        getView().setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        const val LENGTH_SHORT = BaseTransientBottomBar.LENGTH_SHORT
        const val LENGTH_LONG = BaseTransientBottomBar.LENGTH_LONG
        const val LENGTH_INDEFINITE = BaseTransientBottomBar.LENGTH_INDEFINITE

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(LENGTH_SHORT, LENGTH_LONG, LENGTH_INDEFINITE)
        annotation class SnackBarDuration

        private val animationCallback2 =
            object : AnimationContentViewCallback {
                override fun animateContentIn(delay: Int, duration: Int) {
                    /* no animations */
                }

                override fun animateContentOut(delay: Int, duration: Int) {
                    /* no animations */
                }
            }

        @JvmStatic
        fun make(view: View, text: CharSequence, @SnackBarDuration duration: Int): MySnackBar {
            val parent = findSuitableParent(view)
                ?: throw IllegalArgumentException(
                    "No suitable parent found from the given view. Please provide a valid view."
                )
            val content = LayoutInflater.from(view.context).inflate(
                R.layout.item_custom_snackbar,
                parent,
                false
            )
            val binding = ItemCustomSnackbarBinding.bind(content)
            val mySnackBar = MySnackBar(parent, content, animationCallback2)
            mySnackBar.duration = duration
            binding.snackBarText.text = text

            return mySnackBar
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var currentView: View? = view
            var fallback: ViewGroup? = null
            do {
                when (currentView) {
                    is CoordinatorLayout -> return currentView
                    is FrameLayout -> if (currentView.id == android.R.id.content) {
                        return currentView
                    } else {
                        fallback = currentView
                    }
                }

                if (currentView != null) {
                    val parent = currentView.parent
                    currentView = if (parent is View) parent else null
                }
            } while (currentView != null)

            return fallback
        }
    }
}