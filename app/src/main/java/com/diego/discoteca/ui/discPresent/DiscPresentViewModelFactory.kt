package com.diego.discoteca.ui.discPresent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.repository.DiscsRepository

class DiscPresentViewModelFactory(
    private val repository: DiscsRepository,
    private val discPresent: DiscPresent
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscPresentViewModel::class.java)) {
            return DiscPresentViewModel(
                repository = repository,
                discItem = discPresent
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}