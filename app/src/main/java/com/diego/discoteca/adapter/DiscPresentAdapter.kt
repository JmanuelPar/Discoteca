package com.diego.discoteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.databinding.ItemDiscPresentListBinding
import com.diego.discoteca.domain.Disc

class DiscPresentAdapter(private val clickListener: Listener) :
    ListAdapter<Disc, DiscPresentAdapter.ViewHolder>(DISC_PRESENT_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemDiscPresentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Disc, clickListener: Listener) {
            binding.disc = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscPresentListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object {
        val DISC_PRESENT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Disc>() {
            override fun areItemsTheSame(oldItem: Disc, newItem: Disc): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Disc, newItem: Disc): Boolean =
                oldItem == newItem
        }
    }
}