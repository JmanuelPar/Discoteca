package com.diego.discoteca.ui.discResultSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.repository.DiscsRepository
import kotlinx.coroutines.flow.Flow

class DiscResultSearchViewModel(
    private val repository: DiscsRepository,
    private val discItem: DiscPresent
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Disc>>

    private val _discPresent = MutableLiveData<DiscPresent>()
    val discPresent: LiveData<DiscPresent>
        get() = _discPresent

    private val _totalResult = MutableLiveData<Int>()
    val totalResult: LiveData<Int>
        get() = _totalResult

    private val _nBDManually = MutableLiveData<Int>()
    val nBManually: LiveData<Int>
        get() = _nBDManually

    private val _nBScan = MutableLiveData<Int>()
    val nBScan: LiveData<Int>
        get() = _nBScan

    private val _nBSearch = MutableLiveData<Int>()
    val nBSearch: LiveData<Int>
        get() = _nBSearch

    private val _visibilityError = MutableLiveData<Boolean>()
    val visibilityError: LiveData<Boolean>
        get() = _visibilityError

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _buttonRetry = MutableLiveData<Boolean>()
    val buttonRetry: LiveData<Boolean>
        get() = _buttonRetry

    private val _buttonBack = MutableLiveData<Boolean>()
    val buttonBack: LiveData<Boolean>
        get() = _buttonBack

    init {
        pagingDataFlow = searchDisc().cachedIn(viewModelScope)
        _discPresent.value = discItem
        visibilityError(false)
    }

    private fun searchDisc(): Flow<PagingData<Disc>> {
        return repository.getSearchDiscStream(discPresent = discItem)
    }

    fun onButtonBackClicked() {
        _buttonBack.value = true
    }

    fun onButtonBackClickedDone() {
        _buttonBack.value = false
    }

    fun onButtonRetryClicked() {
        visibilityError(false)
        _buttonRetry.value = true
    }

    fun onButtonRetryClickedDone() {
        _buttonRetry.value = false
    }

    fun updateTotalResult(total: Int) {
        _totalResult.value = total
    }

    private fun visibilityError(visibility: Boolean) {
        _visibilityError.value = visibility
    }

    fun showErrorLayout(messageError: String) {
        visibilityError(true)
        _messageError.value = messageError
    }

    fun updateNbPresentDisc(list: ItemSnapshotList<Disc>) {
        _nBDManually.value = getSize(list)
        _nBScan.value = list.filter { disc ->
            disc?.isPresentByScan == true
        }.size
        _nBSearch.value = list.filter { disc ->
            disc?.isPresentBySearch == true
        }.size
    }

    private fun getSize(list: ItemSnapshotList<Disc>): Int {
        val result = mutableListOf<String>()
        list.forEach {
            if (it?.discLight?.name?.isNotEmpty() == true) {
                result.add(it.discLight?.name!!)
            }
        }
        return result.toSet().size
    }
}
