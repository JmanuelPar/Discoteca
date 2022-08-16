package com.diego.discoteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.databinding.ItemCountFormatMediaBinding
import com.diego.discoteca.model.CountFormatMedia

class CountFormatMediaAdapter : ListAdapter<CountFormatMedia, CountFormatMediaAdapter.ViewHolder>(
    COUNT_FORMAT_MEDIA_DIFF_CALLBACK
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCountFormatMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CountFormatMedia) {
            binding.countFormatMedia = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCountFormatMediaBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object {
        val COUNT_FORMAT_MEDIA_DIFF_CALLBACK = object : DiffUtil.ItemCallback<CountFormatMedia>() {
            override fun areItemsTheSame(
                oldItem: CountFormatMedia,
                newItem: CountFormatMedia
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CountFormatMedia,
                newItem: CountFormatMedia
            ): Boolean =
                oldItem == newItem
        }
    }
}