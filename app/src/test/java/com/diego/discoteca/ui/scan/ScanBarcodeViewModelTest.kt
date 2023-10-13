package com.diego.discoteca.ui.scan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diego.discoteca.MainCoroutineRule
import com.diego.discoteca.data.model.DiscResultScan
import com.diego.discoteca.getOrAwaitValue
import com.diego.discoteca.util.Destination
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScanBarcodeViewModelTest {

    private lateinit var scanBarcodeViewModel: ScanBarcodeViewModel
    private val barcode = "barcode_1"

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecuteRule = InstantTaskExecutorRule()

    @Test
    fun test_Configure_Init() {
        scanBarcodeViewModel = ScanBarcodeViewModel(Destination.API)

        val result1 = scanBarcodeViewModel.scanFoundBarcodeIcon.getOrAwaitValue()
        val result2 = scanBarcodeViewModel.progressBarState.getOrAwaitValue()

        assertEquals(false, result1)
        assertEquals(true, result2)
    }

    @Test
    fun test_Api_SearchBarcode() {
        scanBarcodeViewModel = ScanBarcodeViewModel(Destination.API)

        val discResultScan = DiscResultScan(
            barcode,
            Destination.API
        )

        scanBarcodeViewModel.searchBarcode(discResultScan.barcode)

        val result1 = scanBarcodeViewModel.scanFoundBarcodeIcon.getOrAwaitValue()
        val result2 = scanBarcodeViewModel.navigateToDiscResultScan.getOrAwaitValue()

        assertEquals(true, result1)
        assertEquals(discResultScan, result2)
    }

    @Test
    fun test_Database_SearchBarcode() {
        scanBarcodeViewModel = ScanBarcodeViewModel(Destination.DATABASE)

        val discResultScan = DiscResultScan(
            barcode,
            Destination.DATABASE
        )

        scanBarcodeViewModel.searchBarcode(discResultScan.barcode)

        val result1 = scanBarcodeViewModel.scanFoundBarcodeIcon.getOrAwaitValue()
        val result2 = scanBarcodeViewModel.navigateToDiscResultScan.getOrAwaitValue()

        assertEquals(true, result1)
        assertEquals(discResultScan, result2)
    }
}