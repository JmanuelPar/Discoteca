package com.diego.discoteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.R
import com.diego.discoteca.databinding.ItemDiscCardBinding
import com.diego.discoteca.databinding.ItemDiscCardGridBinding
import com.diego.discoteca.domain.Disc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_TYPE_MESSAGE = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class DiscAdapter(
    private val listener: DiscListener,
    private val myLayoutManager: GridLayoutManager,
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DISC_DIFF_CALLBACK) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.MessageItem -> ITEM_VIEW_TYPE_MESSAGE
            is DataItem.DiscItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscViewHolderLinear -> {
                val discItem = getItem(position) as DataItem.DiscItem
                holder.bind(discItem.disc, listener)
            }
            is DiscViewHolderGrid -> {
                val discItem = getItem(position) as DataItem.DiscItem
                holder.bind(discItem.disc, listener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_MESSAGE -> MessageViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> {
                if (myLayoutManager.spanCount == 1) DiscViewHolderLinear.from(parent)
                else DiscViewHolderGrid.from(parent)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun configureAndSubmitList(list: List<Disc>) {
        adapterScope.launch {
            val items = when {
                list.isEmpty() -> {
                    myLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return myLayoutManager.spanCount
                        }
                    }
                    listOf(DataItem.MessageItem)
                }
                else -> {
                    myLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return 1
                        }
                    }
                    list.map { DataItem.DiscItem(it) }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.recycler_view_message, parent, false)
                return MessageViewHolder(view)
            }
        }
    }

    class DiscViewHolderLinear private constructor(private val binding: ItemDiscCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Disc,
            listener: DiscListener
        ) {
            binding.disc = item
            binding.listener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DiscViewHolderLinear {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscCardBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return DiscViewHolderLinear(binding)
            }
        }
    }

    class DiscViewHolderGrid private constructor(private val binding: ItemDiscCardGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Disc,
            listener: DiscListener,
        ) {
            binding.disc = item
            binding.listener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DiscViewHolderGrid {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscCardGridBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return DiscViewHolderGrid(binding)
            }
        }
    }

    interface DiscListener {
        fun onDiscClicked(view: View, disc: Disc)
        fun onDiscDeleteClicked(disc: Disc)
        fun onDiscUpdateClicked(disc: Disc)
        fun onPopupMenuClicked(view: View, disc: Disc)
    }

    companion object {
        val DISC_DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem == newItem
        }
    }
}

sealed class DataItem {
    abstract val id: Long

    data class DiscItem(val disc: Disc) : DataItem() {
        override val id = disc.id
    }

    object MessageItem : DataItem() {
        override val id = Long.MIN_VALUE
    }
}

