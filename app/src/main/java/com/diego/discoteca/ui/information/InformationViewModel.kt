package com.diego.discoteca.ui.information

import androidx.lifecycle.*
import com.diego.discoteca.BuildConfig
import com.diego.discoteca.repository.DiscsRepository

class InformationViewModel(val repository: DiscsRepository) : ViewModel() {

    val countDiscs = repository.countAllDiscs.asLiveData()
    val countFormatMediaList = repository.countFormatMediaList.asLiveData()

    private val _numberVersion = MutableLiveData<String>()
    val numberVersion: LiveData<String>
        get() = _numberVersion

    private val _buttonSearchScan = MutableLiveData<Boolean>()
    val buttonSearchScan: LiveData<Boolean>
        get() = _buttonSearchScan

    val isEmptyDatabase = Transformations.map(countDiscs) { count ->
        count == 0
    }

    init {
        setNumberVersion()
    }

    private fun setNumberVersion() {
        _numberVersion.value = BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE

    }

    fun onButtonSearchScanClicked() {
        _buttonSearchScan.value = true
    }

    fun onButtonSearchScanClickedDone() {
        _buttonSearchScan.value = false
    }
}