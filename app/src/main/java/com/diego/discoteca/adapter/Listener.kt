package com.diego.discoteca.adapter

import android.view.View
import com.diego.discoteca.domain.Disc

class Listener(val clickListener: (view: View, disc: Disc) -> Unit) {
    fun onClick(view: View, disc: Disc) = clickListener(view, disc)
}