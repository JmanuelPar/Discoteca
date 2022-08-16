package com.diego.discoteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.databinding.ItemDiscResultListBinding
import com.diego.discoteca.domain.Disc

class DiscResultAdapter(private val clickListener: Listener) :
    PagingDataAdapter<Disc, DiscResultAdapter.ViewHolder>(DISC_RESULT_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(item, clickListener) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemDiscResultListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Disc, clickListener: Listener) {
            binding.disc = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscResultListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object {
        private val DISC_RESULT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Disc>() {
            override fun areItemsTheSame(oldItem: Disc, newItem: Disc): Boolean =
                oldItem.idDisc == newItem.idDisc

            override fun areContentsTheSame(oldItem: Disc, newItem: Disc): Boolean =
                oldItem == newItem
        }
    }
}