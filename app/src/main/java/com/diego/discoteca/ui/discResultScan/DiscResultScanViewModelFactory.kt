package com.diego.discoteca.ui.discResultScan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.model.DiscResultScan
import com.diego.discoteca.repository.DiscsRepository

class DiscResultScanViewModelFactory(
    private val repository: DiscsRepository,
    private val discResultScan: DiscResultScan
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscResultScanViewModel::class.java)) {
            return DiscResultScanViewModel(
                repository = repository,
                discItem = discResultScan
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}