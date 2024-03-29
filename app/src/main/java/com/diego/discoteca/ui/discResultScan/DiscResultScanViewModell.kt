package com.diego.discoteca.ui.discResultScan

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.data.model.DiscResultScan
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.Destination
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.flow.Flow

class DiscResultScanViewModel(
    private val repository: DiscsRepository,
    private val discItem: DiscResultScan
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Disc>>

    private val _discResultScan = MutableLiveData<DiscResultScan>()
    val discResultScan: LiveData<DiscResultScan>
        get() = _discResultScan

    private val _nBManually = MutableLiveData<Int>()
    val nBManually: LiveData<Int>
        get() = _nBManually

    private val _nBScan = MutableLiveData<Int>()
    val nBScan: LiveData<Int>
        get() = _nBScan

    private val _nBSearch = MutableLiveData<Int>()
    val nBSearch: LiveData<Int>
        get() = _nBSearch

    private val _totalResult = MutableLiveData<UIText?>()
    val totalResult: LiveData<UIText?>
        get() = _totalResult

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _visibilityError = MutableLiveData<Boolean>()
    val visibilityError: LiveData<Boolean>
        get() = _visibilityError

    private val _navigateToDiscResultDetail = MutableLiveData<Pair<View, DiscResultDetail>?>()
    val navigateToDiscResultDetail: LiveData<Pair<View, DiscResultDetail>?>
        get() = _navigateToDiscResultDetail

    private val _navigateToDiscDetail = MutableLiveData<Pair<View, Long>?>()
    val navigateToDiscDetail: MutableLiveData<Pair<View, Long>?>
        get() = _navigateToDiscDetail

    private val _buttonRetry = MutableLiveData<Boolean>()
    val buttonRetry: LiveData<Boolean>
        get() = _buttonRetry

    private val _buttonBack = MutableLiveData<Destination>()
    val buttonBack: MutableLiveData<Destination>
        get() = _buttonBack

    init {
        pagingDataFlow = scanDisc().cachedIn(viewModelScope)
        _discResultScan.value = discItem
        visibilityError(false)
    }

    private fun scanDisc(): Flow<PagingData<Disc>> {
        return when (discItem.destination) {
            Destination.API -> repository.getSearchBarcodeStream(barcode = discItem.barcode)
            // Destination.DATABASE
            else -> repository.getSearchBarcodeDatabase(barcode = discItem.barcode)
        }
    }

    fun updateNbDisc(list: ItemSnapshotList<Disc>) {
        _nBManually.value = getSize(list)
        _nBScan.value = list.filter { disc ->
            disc?.isPresentByScan == true
        }.size
        _nBSearch.value = list.filter { disc ->
            disc?.isPresentBySearch == true
        }.size
    }

    private fun getSize(list: ItemSnapshotList<Disc>): Int {
        val result = mutableListOf<String>()
        list.forEach { disc ->
            if (disc?.isPresentByManually == true) {
                result.add(disc.discLight?.name!!)
            }
        }
        return result.toSet().size
    }

    fun updateTotal(total: Int) {
        _totalResult.value = when (discItem.destination) {
            Destination.API -> UIText.TotalApi(total)
            // Destination.DATABASE
            else -> UIText.TotalDatabase(total)
        }
    }

    fun showErrorLayout(messageError: String) {
        _messageError.value = messageError
        visibilityError(true)
    }

    private fun visibilityError(visibility: Boolean) {
        _visibilityError.value = visibility
    }

    fun onButtonRetryClicked() {
        visibilityError(false)
        _buttonRetry.value = true
    }

    fun onButtonRetryClickedDone() {
        _buttonRetry.value = false
    }

    fun onDiscResultClicked(view: View, disc: Disc) {
        when (discItem.destination) {
            Destination.API -> onDiscResultDetailClicked(
                Pair(
                    view,
                    DiscResultDetail(
                        disc = disc,
                        addBy = AddBy.SCAN
                    )
                )
            )
            // Destination.DATABASE
            else -> onDiscDetailClicked(Pair(view, disc.id))
        }
    }

    private fun onDiscResultDetailClicked(pair: Pair<View, DiscResultDetail>) {
        _navigateToDiscResultDetail.value = pair
    }

    fun onDiscResultDetailClickedDone() {
        _navigateToDiscResultDetail.value = null
    }

    private fun onDiscDetailClicked(pair: Pair<View, Long>) {
        _navigateToDiscDetail.value = pair
    }

    fun onDiscDetailClickedDone() {
        _navigateToDiscDetail.value = null
    }

    fun onButtonBackClicked() {
        _buttonBack.value = discItem.destination
    }

    fun onButtonBackClickedDone() {
        _buttonBack.value = Destination.NONE
    }
}