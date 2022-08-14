package com.diego.discoteca.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.util.Destination

class ScanBarcodeViewModelFactory(private val destination: Destination) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanBarcodeViewModel::class.java)) {
            return ScanBarcodeViewModel(destination) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}