package com.diego.discoteca.ui.updateDisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.repository.DiscsRepository

class UpdateDiscViewModelFactory(
    private val repository: DiscsRepository,
    private val discId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateDiscViewModel::class.java)) {
            return UpdateDiscViewModel(
                repository = repository,
                discId = discId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}