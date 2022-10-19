package com.diego.discoteca.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diego.discoteca.data.PreferencesManager
import kotlinx.coroutines.launch

class MainActivityViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    val nightModeFlow = preferencesManager.nightModeFlow
    val nightMode = nightModeFlow.asLiveData()

    fun onNightModeSelected(mode: Int){
        viewModelScope.launch {
            preferencesManager.updateNightMode(mode)
        }
    }
}