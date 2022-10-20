package com.diego.discoteca.ui.discPresentDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.repository.DiscsRepository

class DiscPresentDetailViewModelFactory(
    private val repository: DiscsRepository,
    private val discPresent: DiscPresent
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscPresentDetailViewModel::class.java)) {
            return DiscPresentDetailViewModel(
                repository = repository,
                discPresent = discPresent
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}