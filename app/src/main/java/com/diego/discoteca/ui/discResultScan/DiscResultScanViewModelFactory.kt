package com.diego.discoteca.ui.discResultScan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.model.DiscResultScan
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.repository.DiscogsRepository

class DiscResultScanViewModelFactory(
    private val repository: DiscRepository,
    private val discogsRepository: DiscogsRepository,
    private val discResultScan: DiscResultScan
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscResultScanViewModel::class.java)) {
            return DiscResultScanViewModel(
                repository = repository,
                discogsRepository = discogsRepository,
                discItem = discResultScan
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}