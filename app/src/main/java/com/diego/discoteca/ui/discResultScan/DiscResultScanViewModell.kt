package com.diego.discoteca.ui.discResultScan

import  android.view.View
import androidx.lifecycle.*
import androidx.paging.*
import com.diego.discoteca.R
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.data.DatabasePagingSourceBarcode
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscResultDetail
import com.diego.discoteca.model.DiscResultScan
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DiscRepository.Companion.DATABASE_PAGE_SIZE
import com.diego.discoteca.repository.DiscogsRepository
import com.diego.discoteca.util.*
import kotlinx.coroutines.flow.*

class DiscResultScanViewModel(private val discItem: DiscResultScan) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Disc>>

    private val _discScan = MutableLiveData<DiscResultScan>()
    val discScan: LiveData<DiscResultScan>
        get() = _discScan

    private val _nBManually = MutableLiveData<Int>()
    val nBManually: LiveData<Int>
        get() = _nBManually

    private val _nBScan = MutableLiveData<Int>()
    val nBScan: LiveData<Int>
        get() = _nBScan

    private val _nBSearch = MutableLiveData<Int>()
    val nBSearch: LiveData<Int>
        get() = _nBSearch

    private val _messageResult = MutableLiveData<String>()
    val messageResult: LiveData<String>
        get() = _messageResult

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

    private val _navigateTo = MutableLiveData<String?>()
    val navigateTo: MutableLiveData<String?>
        get() = _navigateTo

    init {
        pagingDataFlow = scanDisc().cachedIn(viewModelScope)
        _discScan.value = discItem
        visibilityError(false)
    }

    private fun scanDisc(): Flow<PagingData<Disc>> {
        return when (discItem.code) {
            API -> DiscogsRepository(DiscogsApi.retrofitService).getSearchBarcodeStream(
                barcode = discItem.barcode
            )
            else -> getPagerDatabaseBarcode()
        }
    }

    private fun getPagerDatabaseBarcode() =
        Pager(
            config = PagingConfig(
                pageSize = DATABASE_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DatabasePagingSourceBarcode(barcode = discItem.barcode)
            }
        ).flow

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
        _messageResult.value = when (discItem.code) {
            API -> MyApp.res.getQuantityString(
                R.plurals.plural_total_result,
                total,
                total
            )
            // DATABASE
            else -> MyApp.res.getQuantityString(
                R.plurals.plural_nb_disc_present_search_barcode,
                total,
                total
            )
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
        when (discItem.code) {
            API -> onDiscResultDetailClicked(
                Pair(view, DiscResultDetail(disc, SCAN))
            )
            DATABASE -> onDiscDetailClicked(Pair(view, disc.id))
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

    fun onNavigateTo() {
        _navigateTo.value = discItem.code
    }

    fun onNavigateToDone() {
        _navigateTo.value = null
    }
}