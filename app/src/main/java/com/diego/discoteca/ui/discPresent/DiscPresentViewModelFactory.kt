package com.diego.discoteca.ui.discPresent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.model.DiscPresent
import com.diego.discoteca.repository.DiscRepository

class DiscPresentViewModelFactory(
    private val repository: DiscRepository,
    private val discPresent: DiscPresent
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscPresentViewModel::class.java)) {
            return DiscPresentViewModel(repository, discPresent) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}