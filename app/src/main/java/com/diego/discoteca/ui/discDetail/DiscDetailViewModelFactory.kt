package com.diego.discoteca.ui.discDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.repository.DiscRepository

class DiscDetailViewModelFactory(
    private val repository: DiscRepository,
    private val discKey: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscDetailViewModel::class.java)) {
            return DiscDetailViewModel(repository, discKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}