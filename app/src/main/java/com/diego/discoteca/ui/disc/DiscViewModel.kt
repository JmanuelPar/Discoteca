package com.diego.discoteca.ui.disc

import androidx.lifecycle.*
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DiscViewModel(
    private val repository: DiscsRepository,
    private val preferencesManager: PreferencesManager,
    uiText: UIText?,
    private val idAdded: Long
) : ViewModel() {

    val searchQueryFlow = MutableStateFlow("")
    val sortOrderPreferencesFlow = preferencesManager.sortOrderFlow
    val gridModeFlow = preferencesManager.gridModeFlow

    private val listDiscsFlow = combine(
        searchQueryFlow,
        sortOrderPreferencesFlow,
        ::Pair
    ).flatMapLatest { (searchQuery, sortOrderPreferences) ->
        repository.getAllDiscs(
            searchQuery = searchQuery,
            sortOrder = sortOrderPreferences.sortOrder
        )
    }

    val listDiscs = listDiscsFlow.asLiveData()

    private val isNotEmpty = Transformations.map(listDiscs) { allDiscs ->
        allDiscs.isNotEmpty()
    }

    private val _isSearch = MutableLiveData(false)
    val isSearch: LiveData<Boolean>
        get() = _isSearch

    val isDisplayItem = isNotEmpty.asFlow().combine(isSearch.asFlow()) { isNotEmpty, isSearch ->
        when {
            isNotEmpty && !isSearch -> true
            !isNotEmpty && !isSearch -> false
            isNotEmpty && isSearch -> false
            else -> false
        }
    }.asLiveData()

    val gridMode = gridModeFlow.asLiveData()

    private val _pendingQuery = MutableLiveData("")
    val pendingQuery: LiveData<String>
        get() = _pendingQuery

    private val _id = MutableLiveData<Long>()
    val id: LiveData<Long>
        get() = _id

    private val _scrollToPosition = MutableLiveData<Int?>()
    val scrollToPosition: LiveData<Int?>
        get() = _scrollToPosition

    private val _buttonDeleteDisc = MutableLiveData<Long?>()
    val buttonDeleteDisc: LiveData<Long?>
        get() = _buttonDeleteDisc

    private val _buttonUpdateDisc = MutableLiveData<Long?>()
    val buttonUpdateDisc: LiveData<Long?>
        get() = _buttonUpdateDisc

    private val _navigateToUpdateDisc = MutableLiveData<Long?>()
    val navigateToUpdateDisc: LiveData<Long?>
        get() = _navigateToUpdateDisc

    private val _showSnackBar = MutableLiveData<UIText?>()
    val showSnackBar: LiveData<UIText?>
        get() = _showSnackBar

    init {
        onId(idAdded)
        onShowSnackBar(uiText)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onGridModeSelected(gridMode: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateGridMode(gridMode)
        }
    }

    fun updateSearch(query: String) {
        searchQueryFlow.value = query
    }

    fun updatePendingQuery(pendingQuery: String) {
        _pendingQuery.value = pendingQuery
    }

    fun updatePendingQueryDone() {
        _pendingQuery.value = ""
    }

    private fun onId(id: Long) {
        _id.value = id
    }

    private fun onIdDone() {
        _id.value = -1L
    }

    fun buttonDeleteDiscClicked(discId: Long) {
        _buttonDeleteDisc.value = discId
    }

    fun buttonDeleteDiscClickedDone() {
        _buttonDeleteDisc.value = null
    }

    fun buttonUpdateDiscClicked(discId: Long) {
        _buttonUpdateDisc.value = discId
    }

    fun buttonUpdateDiscClickedDone() {
        _buttonUpdateDisc.value = null
    }

    fun yesDeleteDiscClicked(discId: Long) {
        viewModelScope.launch {
            deleteDisc(discId)
            onShowSnackBar(UIText.DiscDeleted)
        }
    }

    fun yesUpdateDiscClicked(discId: Long) {
        _navigateToUpdateDisc.value = discId
    }

    fun yesUpdateDiscClickedDone() {
        _navigateToUpdateDisc.value = null
    }

    private suspend fun deleteDisc(discId: Long) {
        repository.deleteById(discId)
    }

    private fun onShowSnackBar(uiText: UIText?) {
        _showSnackBar.value = uiText
    }

    fun onShowSnackBarDone() {
        _showSnackBar.value = null
    }

    private fun onScrollToPosition(position: Int) {
        _scrollToPosition.value = position
    }

    fun onScrollToPositionDone() {
        _scrollToPosition.value = null
    }

    fun scrollToPosition(list: List<Disc>) {
        if (list.isNotEmpty()) {
            when {
                id.value != -1L -> {
                    var position = -1
                    list.forEach {
                        if (it.id == idAdded) {
                            position = list.indexOf(it)
                        }
                    }

                    when (position) {
                        -1 -> onScrollToPosition(0)
                        else -> onScrollToPosition(position)
                    }
                    onIdDone()
                }
            }
        }
    }

    fun updateIsSearch(isSearch: Boolean) {
        _isSearch.value = isSearch
    }
}