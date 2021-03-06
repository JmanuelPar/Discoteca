package com.diego.discoteca.ui.updateDisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.repository.DiscRepository

class UpdateDiscViewModelFactory(
    private val repository: DiscRepository,
    private val discId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateDiscViewModel::class.java)) {
            return UpdateDiscViewModel(repository, discId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}