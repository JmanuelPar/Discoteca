package com.diego.discoteca.ui.discResultDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.repository.DiscsRepository

class DiscResultDetailViewModelFactory(
    private val repository: DiscsRepository,
    private val discResultDetail: DiscResultDetail
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscResultDetailViewModel::class.java)) {
            return DiscResultDetailViewModel(
                repository = repository,
                discItem = discResultDetail
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}