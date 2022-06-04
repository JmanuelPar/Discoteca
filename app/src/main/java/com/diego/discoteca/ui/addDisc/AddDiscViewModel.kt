package com.diego.discoteca.ui.addDisc

import android.text.Editable
import androidx.lifecycle.*
import com.diego.discoteca.R
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscAdd
import com.diego.discoteca.model.DiscPresent
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.*
import kotlinx.coroutines.launch

class AddDiscViewModel(private val repository: DiscRepository) : ViewModel() {

    private val _discNameArtist = MutableLiveData<String>()
    private val discNameArtist: LiveData<String>
        get() = _discNameArtist

    private val _discTitle = MutableLiveData<String>()
    private val discTitle: LiveData<String>
        get() = _discTitle

    private val _discYear = MutableLiveData<String>()
    private val discYear: LiveData<String>
        get() = _discYear

    private val _errorMessageDiscArtist = MutableLiveData<String?>()
    val errorMessageDiscArtist: LiveData<String?>
        get() = _errorMessageDiscArtist

    private val _errorMessageDiscTitle = MutableLiveData<String?>()
    val errorMessageDiscTitle: LiveData<String?>
        get() = _errorMessageDiscTitle

    private val _errorMessageDiscYear = MutableLiveData<String?>()
    val errorMessageDiscYear: LiveData<String?>
        get() = _errorMessageDiscYear

    private val _showBottomSheet = MutableLiveData<DiscAdd?>()
    val showBottomSheet: LiveData<DiscAdd?>
        get() = _showBottomSheet

    private val _navigateToDisc = MutableLiveData<Pair<String, Long>?>()
    val navigateToDisc: LiveData<Pair<String, Long>?>
        get() = _navigateToDisc

    private val _navigateToDiscPresent = MutableLiveData<DiscPresent?>()
    val navigateToDiscPresent: LiveData<DiscPresent?>
        get() = _navigateToDiscPresent

    private val _navigateToDiscResultSearch = MutableLiveData<DiscPresent?>()
    val navigateToDiscResultSearch: LiveData<DiscPresent?>
        get() = _navigateToDiscResultSearch

    val isVisibleEmDiscArt = Transformations.map(errorMessageDiscArtist) {
        it?.isNotEmpty()
    }

    val isVisibleEmDiscTitle = Transformations.map(errorMessageDiscTitle) {
        it?.isNotEmpty()
    }

    val isVisibleEmDiscYear = Transformations.map(errorMessageDiscYear) {
        it?.isNotEmpty()
    }

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
        checkDiscYear()
    }

    private fun onNavigateToDisc(snackBarMessage: String, id: Long) {
        _navigateToDisc.value = Pair(snackBarMessage, id)
    }

    fun onNavigateToDiscDone() {
        _navigateToDisc.value = null
    }

    private fun onNavigateToDiscResultSearch(discPresent: DiscPresent) {
        _navigateToDiscResultSearch.value = discPresent
    }

    fun onNavigateToDiscResultSearchDone() {
        _navigateToDiscResultSearch.value = null
    }

    private fun onNavigateToDiscPresent(discPresent: DiscPresent) {
        _navigateToDiscPresent.value = discPresent
    }

    fun onNavigateToDiscPresentDone() {
        _navigateToDiscPresent.value = null
    }

    private fun onShowBottomSheet(discAdd: DiscAdd) {
        _showBottomSheet.value = discAdd
    }

    fun onShowBottomSheetDone() {
        _showBottomSheet.value = null
    }

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

    private fun checkDiscYear() = when {
        discYear.value?.trim().isNullOrEmpty() -> {
            _errorMessageDiscYear.value = MyApp.res.getString(R.string.indicate_year_disc)
            false
        }
        discYear.value?.length!! < 4 || discYear.value!! < "1900" -> {
            _errorMessageDiscYear.value = MyApp.res.getString(R.string.no_valid_year)
            false
        }
        else -> {
            _errorMessageDiscYear.value = null
            true
        }
    }

    private fun isValid() = checkDiscArtist() && checkDiscTitle() && checkDiscYear()

    fun onButtonAddClicked() {
        if (isValid()) {
            onShowBottomSheet(
                DiscAdd(
                    name = discNameArtist.value!!.stringProcess(),
                    title = discTitle.value!!.stringProcess(),
                    year = discYear.value!!.trim(),
                    addBy = MANUALLY
                )
            )
        }
    }

    fun onButtonSearchClicked() {
        if (isValid()) {
            onShowBottomSheet(
                DiscAdd(
                    name = discNameArtist.value!!.stringProcess(),
                    title = discTitle.value!!.stringProcess(),
                    year = discYear.value!!.trim(),
                    addBy = SEARCH
                )
            )
        }
    }

    fun addDisc(discAdd: DiscAdd) {
        viewModelScope.launch {
            val listDb = getListDiscDbPresent(discAdd)
            when {
                listDb.isEmpty() -> {
                    // Add disc in database manually
                    val id = addDiscDatabase(
                        Disc(
                            name = discAdd.name,
                            title = discAdd.title,
                            year = discAdd.year,
                            addBy = discAdd.addBy
                        )
                    )

                    onNavigateToDisc(
                        snackBarMessage = MyApp.res.getString(R.string.disc_added),
                        id = id
                    )
                }
                else -> onNavigateToDiscPresent(DiscPresent(listDb, discAdd))
            }
        }
    }

    fun searchDisc(discAdd: DiscAdd) {
        viewModelScope.launch {
            val listDb = getListDiscDbPresent(discAdd)
            val discPresent = DiscPresent(listDb, discAdd)
            when {
                listDb.isEmpty() -> onNavigateToDiscResultSearch(discPresent)
                else -> onNavigateToDiscPresent(discPresent)
            }
        }
    }

    private suspend fun addDiscDatabase(disc: Disc) =
        repository.insertLong(disc)


    /* Get list of disc in database added by manually/scan/search
    with artist/group name + title + year filled by the user */
    private suspend fun getListDiscDbPresent(discAdd: DiscAdd) =
        repository.getListDiscDbPresent(
            discAdd.name,
            discAdd.title,
            discAdd.year
        )
}