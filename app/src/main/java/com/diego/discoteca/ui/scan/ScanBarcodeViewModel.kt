package com.diego.discoteca.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.discoteca.model.*
import com.diego.discoteca.util.Destination
import kotlinx.coroutines.launch

class ScanBarcodeViewModel(val destination: Destination) : ViewModel() {

    private val _progressBarState = MutableLiveData<Boolean>()
    val progressBarState: LiveData<Boolean>
        get() = _progressBarState

    private val _navigateToDiscResultScan = MutableLiveData<DiscResultScan?>()
    val navigateToDiscResultScan: LiveData<DiscResultScan?>
        get() = _navigateToDiscResultScan

    private val _scanFoundBarcodeIcon = MutableLiveData<Boolean>()
    val scanFoundBarcodeIcon: LiveData<Boolean>
        get() = _scanFoundBarcodeIcon

    init {
        configure()
    }

    private fun configure() {
        _scanFoundBarcodeIcon.value = false
        _progressBarState.value = true
    }

    private fun onNavigateToDiscResultScan(discResultScan: DiscResultScan) {
        _navigateToDiscResultScan.value = discResultScan
    }

    fun onNavigateToDiscResultScanDone() {
        _navigateToDiscResultScan.value = null
    }

    fun searchBarcode(barcode: String) {
        viewModelScope.launch {
            _scanFoundBarcodeIcon.value = true
            onNavigateToDiscResultScan(
                DiscResultScan(
                    barcode = barcode,
                    destination = destination
                )
            )
        }
    }
}