package com.diego.discoteca.ui.disc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.UIText

class DiscViewModelFactory(
    private val repository: DiscsRepository,
    private val preferencesManager: PreferencesManager,
    private val uiText: UIText?,
    private val idAdded: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscViewModel::class.java)) {
            return DiscViewModel(
                repository = repository,
                preferencesManager = preferencesManager,
                uiText = uiText,
                idAdded = idAdded
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
