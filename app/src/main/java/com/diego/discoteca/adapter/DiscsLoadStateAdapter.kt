package com.diego.discoteca.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class DiscsLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<DiscsLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: DiscsLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): DiscsLoadStateViewHolder {
        return DiscsLoadStateViewHolder.create(parent, retry)
    }
}