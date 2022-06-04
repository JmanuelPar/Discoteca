package com.diego.discoteca.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScanBarcodeViewModelFactory(private val code: String) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanBarcodeViewModel::class.java)) {
            return ScanBarcodeViewModel(code) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}