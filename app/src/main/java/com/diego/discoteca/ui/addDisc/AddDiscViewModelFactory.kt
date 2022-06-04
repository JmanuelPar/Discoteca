package com.diego.discoteca.ui.addDisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.repository.DiscRepository

class AddDiscViewModelFactory(private val repository: DiscRepository) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDiscViewModel::class.java)) {
            return AddDiscViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
