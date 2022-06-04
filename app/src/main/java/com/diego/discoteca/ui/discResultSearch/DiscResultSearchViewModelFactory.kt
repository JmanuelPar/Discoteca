package com.diego.discoteca.ui.discResultSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.model.DiscPresent

class DiscResultSearchViewModelFactory(private val discPresent: DiscPresent) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscResultSearchViewModel::class.java)) {
            return DiscResultSearchViewModel(discPresent) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}