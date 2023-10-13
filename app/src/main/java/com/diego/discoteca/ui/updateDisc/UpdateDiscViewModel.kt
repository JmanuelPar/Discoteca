package com.diego.discoteca.ui.updateDisc

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.stringProcess
import kotlinx.coroutines.launch

class UpdateDiscViewModel(
    private val repository: DiscsRepository,
    discId: Long
) : ViewModel() {

    private var disc: LiveData<Disc> = repository.getDiscWithId(discId)
    fun getDisc() = disc

    private val _discNameArtist = MutableLiveData<String>()
    private val discNameArtist: LiveData<String>
        get() = _discNameArtist

    private val _discTitle = MutableLiveData<String>()
    private val discTitle: LiveData<String>
        get() = _discTitle

    private val _discYear = MutableLiveData<String>()
    private val discYear: LiveData<String>
        get() = _discYear

    private val _errorMessageDiscArtist = MutableLiveData<UIText?>()
    val errorMessageDiscArtist: LiveData<UIText?>
        get() = _errorMessageDiscArtist

    private val _errorMessageDiscTitle = MutableLiveData<UIText?>()
    val errorMessageDiscTitle: LiveData<UIText?>
        get() = _errorMessageDiscTitle

    private val _errorMessageDiscYear = MutableLiveData<UIText?>()
    val errorMessageDiscYear: LiveData<UIText?>
        get() = _errorMessageDiscYear

    private val _navigateToDisc = MutableLiveData<Long?>()
    val navigateToDisc: LiveData<Long?>
        get() = _navigateToDisc

    private val _showBottomSheet = MutableLiveData<Disc?>()
    val showBottomSheet: LiveData<Disc?>
        get() = _showBottomSheet

    fun setDiscArtist(editable: Editable) {
        _discNameArtist.value = editable.toString()
        checkDiscArtist()
    }

    fun setDiscTitle(editable: Editable) {
        _discTitle.value = editable.toString()
        checkDiscTitle()
    }

    fun setDiscYear(editable: Editable) {
        _discYear.value = editable.toString()
        checkDiscYear(true)
    }

    fun updateButtonClicked() {
        if (isValid()) {
            viewModelScope.launch {
                val disc = disc.value ?: return@launch
                val discUpdate = disc.copy(
                    name = discNameArtist.value!!.stringProcess(),
                    title = discTitle.value!!.stringProcess(),
                    year = discYear.value!!
                )
                _showBottomSheet.value = discUpdate
            }
        }
    }

    fun updateDisc(discUpdate: Disc) {
        viewModelScope.launch {
            updateDiscDatabase(discUpdate)
            onNavigateToDisc(discUpdate.id)
        }
    }

    private fun isValid() = checkDiscArtist() && checkDiscTitle() && checkDiscYear(false)

    private fun checkDiscArtist(): Boolean {
        if (discNameArtist.value?.trim().isNullOrEmpty()) {
            _errorMessageDiscArtist.value = UIText.DiscArtistNameIndicate
            return false
        } else {
            _errorMessageDiscArtist.value = null
        }
        return true
    }

    private fun checkDiscTitle(): Boolean {
        if (discTitle.value?.trim().isNullOrEmpty()) {
            _errorMessageDiscTitle.value = UIText.DiscTitleIndicate
            return false
        } else {
            _errorMessageDiscTitle.value = null
        }
        return true
    }

    private fun checkDiscYear(isFromEditable: Boolean): Boolean {
        when {
            discYear.value?.trim().isNullOrEmpty() -> {
                _errorMessageDiscYear.value = UIText.DiscYearIndicate
                return false
            }

            when {
                isFromEditable -> discYear.value!! < "1"
                        || (discYear.value?.length!! == 4 && discYear.value!! < "1900")

                else -> discYear.value?.length!! < 4 || discYear.value!! < "1900"
            } -> {
                _errorMessageDiscYear.value = UIText.NoValidDiscYear
                return false
            }

            else -> {
                _errorMessageDiscYear.value = null
                return true
            }
        }
    }

    private suspend fun updateDiscDatabase(discUpdate: Disc) {
        repository.update(discUpdate)
    }

    private fun onNavigateToDisc(id: Long) {
        _navigateToDisc.value = id
    }

    fun onNavigateToDiscDone() {
        _navigateToDisc.value = null
    }

    fun showBottomSheetDone() {
        _showBottomSheet.value = null
    }
}