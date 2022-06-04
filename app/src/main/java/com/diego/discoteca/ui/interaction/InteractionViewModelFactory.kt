package com.diego.discoteca.ui.interaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.repository.DiscRepository

class InteractionViewModelFactory(private val repository: DiscRepository) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InteractionViewModel::class.java)) {
            return InteractionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}