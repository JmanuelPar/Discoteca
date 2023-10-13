package com.diego.discoteca.ui.discPresent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DiscPresentViewModel(
    private val repository: DiscsRepository,
    val discItem: DiscPresent
) : ViewModel() {

    private val _discPresent = MutableLiveData<DiscPresent>()
    val discPresent: LiveData<DiscPresent>
        get() = _discPresent

    private val _listDisc = MutableLiveData<List<Disc>>()
    val listDisc: LiveData<List<Disc>>
        get() = _listDisc

    private val _addBy = MutableLiveData<AddBy>()
    private val addBy: LiveData<AddBy>
        get() = _addBy

    val nBTotal = listDisc.map { list -> list.size }

    val visibilityLayout = listDisc.map { list ->
        list.filter { disc -> disc.isPresentByManually == true }.size
    }.asFlow()
        .combine(addBy.asFlow()) { nB, addBy ->
            when (addBy) {
                AddBy.MANUALLY -> true
                // AddBy.SEARCH
                else -> nB == 0
            }
        }.asLiveData()

    private val _navigateToDisc = MutableLiveData<Pair<UIText, Long>?>()
    val navigateToDisc: LiveData<Pair<UIText, Long>?>
        get() = _navigateToDisc

    private val _navigateToDiscResultSearch = MutableLiveData<DiscPresent?>()
    val navigateToDiscResultSearch: LiveData<DiscPresent?>
        get() = _navigateToDiscResultSearch

    init {
        configure()
    }

    private fun configure() {
        _discPresent.value = discItem

        /*discItem.list = list of Disc from Room Database
          see getListDiscDbPresent in AddDiscViewModel*/
        _listDisc.value = discItem.list.onEach { disc ->
            when (disc.addBy) {
                AddBy.MANUALLY -> disc.isPresentByManually = true
                AddBy.SCAN -> disc.isPresentByScan = true
                // AddBy.SEARCH
                else -> disc.isPresentBySearch = true
            }
        }.sortedByDescending { disc ->
            disc.isPresentByManually == true
        }

        /*discItem.discAdd.addBy = AddBy.MANUALLY or AddBy.SEARCH
        see onButtonAddClicked, onButtonSearchClicked in AddDiscViewModel*/
        _addBy.value = discItem.discAdd.addBy
    }

    fun onButtonAddClicked() {
        viewModelScope.launch {
            when (discItem.discAdd.addBy) {
                AddBy.MANUALLY -> {
                    val id = addDiscDatabase()
                    onNavigateToDisc(
                        uiText = UIText.DiscAdded,
                        id = id
                    )
                }
                // AddBy.SEARCH
                else -> onNavigateToDiscResultSearch()
            }
        }
    }

    fun onButtonCancelClicked() {
        onNavigateToDisc(
            uiText = getSnackBarMessage(),
            id = discItem.list[0].id
        )
    }

    fun onButtonOkClicked() {
        onNavigateToDisc(
            uiText = getSnackBarMessage(),
            id = discItem.list[0].id
        )
    }

    private fun getSnackBarMessage() =
        when (discItem.list.size) {
            1 -> UIText.DiscAlreadyPresentOne
            else -> UIText.DiscAlreadyPresentMore
        }

    private fun onNavigateToDisc(uiText: UIText, id: Long) {
        _navigateToDisc.value = Pair(uiText, id)
    }

    fun onNavigateToDiscDone() {
        _navigateToDisc.value = null
    }

    private fun onNavigateToDiscResultSearch() {
        _navigateToDiscResultSearch.value = discItem
    }

    fun onNavigateToDiscResultSearchDone() {
        _navigateToDiscResultSearch.value = null
    }

    private suspend fun addDiscDatabase() =
        repository.insertLong(
            Disc(
                name = discItem.discAdd.name,
                title = discItem.discAdd.title,
                year = discItem.discAdd.year,
                addBy = discItem.discAdd.addBy
            )
        )
}