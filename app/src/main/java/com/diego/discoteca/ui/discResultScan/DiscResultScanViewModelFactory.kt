package com.diego.discoteca.ui.discResultScan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.model.DiscResultScan

class DiscResultScanViewModelFactory(private val discScan: DiscResultScan) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscResultScanViewModel::class.java)) {
            return DiscResultScanViewModel(discScan) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}