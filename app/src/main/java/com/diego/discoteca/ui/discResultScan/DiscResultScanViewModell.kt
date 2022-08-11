package com.diego.discoteca.ui.discResultScan

import  android.view.View
import androidx.lifecycle.*
import androidx.paging.*
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscResultDetail
import com.diego.discoteca.model.DiscResultScan
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.repository.DiscogsRepository
import com.diego.discoteca.util.*
import kotlinx.coroutines.flow.*

class DiscResultScanViewModel(
    private val repository: DiscRepository,
    private val discogsRepository: DiscogsRepository,
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

    private val _navigateTo = MutableLiveData<String?>()
    val navigateTo: MutableLiveData<String?>
        get() = _navigateTo

    init {
        pagingDataFlow = scanDisc().cachedIn(viewModelScope)
        _discResultScan.value = discItem
        visibilityError(false)
    }

    // DiscogsRepository(DiscogsApi.retrofitService)
    private fun scanDisc(): Flow<PagingData<Disc>> {
        return when (discItem.code) {
            API -> discogsRepository.getSearchBarcodeStream(
                repository = repository,
                barcode = discItem.barcode
            )
            else -> discogsRepository.getSearchBarcodeDatabase(
                repository = repository,
                barcode = discItem.barcode
            )
        }
    }

    /*  private fun getPagerDatabaseBarcode() =
          Pager(
              config = PagingConfig(
                  pageSize = DATABASE_PAGE_SIZE,
                  enablePlaceholders = false
              ),
              pagingSourceFactory = {
                  DatabasePagingSourceBarcode(barcode = discItem.barcode)
              }
          ).flow*/

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

    /*fun updateTotal(total: Int) {
        _messageResult.value = when (discItem.code) {
            API -> DiscotecaApplication.res.getQuantityString(
                R.plurals.plural_total_api_result,
                total,
                total
            )
            // DATABASE
            else -> DiscotecaApplication.res.getQuantityString(
                R.plurals.plural_total_database_result,
                total,
                total
            )
        }
    }*/

    fun updateTotal(total: Int) {
        _totalResult.value = when (discItem.code) {
            API -> UIText.TotalApi(total)
            // DATABASE
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