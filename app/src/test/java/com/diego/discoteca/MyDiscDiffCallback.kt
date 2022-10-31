package com.diego.discoteca

import androidx.recyclerview.widget.DiffUtil
import com.diego.discoteca.data.domain.Disc

class MyDiscDiffCallback : DiffUtil.ItemCallback<Disc>() {

    override fun areItemsTheSame(oldItem: Disc, newItem: Disc): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Disc, newItem: Disc): Boolean {
        return oldItem == newItem
    }
}