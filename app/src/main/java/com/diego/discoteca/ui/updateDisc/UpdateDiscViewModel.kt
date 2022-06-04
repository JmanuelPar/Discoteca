package com.diego.discoteca.ui.updateDisc

import android.text.Editable
import androidx.lifecycle.*
import com.diego.discoteca.R
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.stringProcess
import kotlinx.coroutines.launch

class UpdateDiscViewModel(
    private val repository: DiscRepository,
    discId: Long
) : ViewModel() {

    private var disc: LiveData<Disc> = repository.getDiscWithId(discId)
    fun getDisc() = disc
    private lateinit var discUpdate: Disc

    private val _discNameArtist = MutableLiveData<String>()
    private val discNameArtist: LiveData<String>
        get() = _discNameArtist

    private val _errorMessageDiscArtist = MutableLiveData<String?>()
    val errorMessageDiscArtist: LiveData<String?>
        get() = _errorMessageDiscArtist

    private val _discTitle = MutableLiveData<String>()
    private val discTitle: LiveData<String>
        get() = _discTitle

    private val _errorMessageDiscTitle = MutableLiveData<String?>()
    val errorMessageDiscTitle: LiveData<String?>
        get() = _errorMessageDiscTitle

    private val _discYear = MutableLiveData<String>()
    private val discYear: LiveData<String>
        get() = _discYear

    private val _errorMessageDiscYear = MutableLiveData<String?>()
    val errorMessageDiscYear: LiveData<String?>
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

    val isVisibleEmDiscArt = Transformations.map(errorMessageDiscArtist) {
        it?.isNotEmpty()
    }

    val isVisibleEmDiscTitle = Transformations.map(errorMessageDiscTitle) {
        it?.isNotEmpty()
    }

    val isVisibleEmDiscYear = Transformations.map(errorMessageDiscYear) {
        it?.isNotEmpty()
    }

    fun updateButtonClicked() {
        if (isValid()) {
            viewModelScope.launch {
                val disc = disc.value ?: return@launch
                discUpdate = Disc(
                    id = disc.id,
                    name = discNameArtist.value!!.stringProcess(),
                    title = discTitle.value!!.stringProcess(),
                    year = discYear.value!!,
                    country = disc.country,
                    format = disc.format,
                    formatMedia = disc.formatMedia,
                    coverImage = disc.coverImage,
                    barcode = disc.barcode,
                    addBy = disc.addBy
                )
                _showBottomSheet.value = discUpdate
            }
        }
    }

    fun updateDisc() {
        viewModelScope.launch {
            updateDiscDatabase()
            onNavigateToDisc(discUpdate.id)
        }
    }

    private fun isValid() = checkDiscArtist() && checkDiscTitle() && checkDiscYear(false)

    private fun checkDiscArtist(): Boolean {
        if (discNameArtist.value?.trim().isNullOrEmpty()) {
            _errorMessageDiscArtist.value = MyApp.res.getString(R.string.indicate_artist_group_name)
            return false
        } else {
            _errorMessageDiscArtist.value = null
        }
        return true
    }

    private fun checkDiscTitle(): Boolean {
        if (discTitle.value?.trim().isNullOrEmpty()) {
            _errorMessageDiscTitle.value = MyApp.res.getString(R.string.indicate_title_disc)
            return false
        } else {
            _errorMessageDiscTitle.value = null
        }
        return true
    }

    private fun checkDiscYear(isFromEditable: Boolean): Boolean {
        when {
            discYear.value?.trim().isNullOrEmpty() -> {
                _errorMessageDiscYear.value = MyApp.res.getString(R.string.indicate_year_disc)
                return false
            }
            when {
                isFromEditable -> discYear.value!! < "1"
                        || (discYear.value?.length!! == 4 && discYear.value!! < "1900")
                else -> discYear.value?.length!! < 4 || discYear.value!! < "1900"
            } -> {
                _errorMessageDiscYear.value = MyApp.res.getString(R.string.no_valid_year)
                return false
            }
            else -> {
                _errorMessageDiscYear.value = null
                return true
            }
        }
    }

    private suspend fun updateDiscDatabase() {
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