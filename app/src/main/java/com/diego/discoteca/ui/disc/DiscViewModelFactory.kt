package com.diego.discoteca.ui.disc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.repository.DiscRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DiscViewModelFactory(
    private val repository: DiscRepository,
    private val preferencesManager: PreferencesManager,
    private val snackBarMessage: String,
    private val idAdded: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscViewModel::class.java)) {
            return DiscViewModel(
                repository,
                preferencesManager,
                snackBarMessage,
                idAdded
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
