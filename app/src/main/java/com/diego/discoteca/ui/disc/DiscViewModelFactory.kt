package com.diego.discoteca.ui.disc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DiscViewModelFactory(
    private val repository: DiscRepository,
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
