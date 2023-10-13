package com.diego.discoteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.R
import com.diego.discoteca.databinding.DiscsPagingLoadStateFooterViewBinding
import retrofit2.HttpException
import java.io.IOException

class DiscsLoadStateViewHolder(
    private val binding: DiscsPagingLoadStateFooterViewBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.buttonRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = when (loadState.error) {
                is IOException -> binding.errorMsg.context.getString(R.string.no_connect_message)
                is HttpException -> String.format(
                    binding.errorMsg.context.getString(R.string.error_result_message),
                    loadState.error.localizedMessage
                )

                else -> binding.errorMsg.context.getString(R.string.error_result_message_unknown)
            }
        }

        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.buttonRetry.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): DiscsLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.discs_paging_load_state_footer_view,
                    parent,
                    false
                )
            val binding = DiscsPagingLoadStateFooterViewBinding.bind(view)
            return DiscsLoadStateViewHolder(binding, retry)
        }
    }
}