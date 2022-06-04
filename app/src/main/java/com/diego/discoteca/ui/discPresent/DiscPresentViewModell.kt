package com.diego.discoteca.ui.discPresent

import androidx.lifecycle.*
import com.diego.discoteca.R
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.DiscPresent
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DiscPresentViewModel(
    private val repository: DiscRepository,
    val discItem: DiscPresent
) : ViewModel() {

    private val _discPresent = MutableLiveData<DiscPresent>()
    val discPresent: LiveData<DiscPresent>
        get() = _discPresent

    private val _listDisc = MutableLiveData<List<Disc>>()
    val listDisc: LiveData<List<Disc>>
        get() = _listDisc

    private val _addBy = MutableLiveData<Int>()
    private val addBy: LiveData<Int>
        get() = _addBy

    val nBTotal = Transformations.map(listDisc) { list -> list.size }

    val visibilityLayout = Transformations.map(listDisc) { list ->
        list.filter { disc -> disc.isPresentByManually == true }.size
    }.asFlow()
        .combine(addBy.asFlow()) { nB, addBy ->
            when (addBy) {
                MANUALLY -> true
                else -> nB == 0
            }
        }.asLiveData()

    private val _navigateToDisc = MutableLiveData<Pair<String, Long>?>()
    val navigateToDisc: LiveData<Pair<String, Long>?>
        get() = _navigateToDisc

    private val _navigateToDiscResultSearch = MutableLiveData<DiscPresent?>()
    val navigateToDiscResultSearch: LiveData<DiscPresent?>
        get() = _navigateToDiscResultSearch

    init {
        configure()
    }

    private fun configure() {
        _discPresent.value = discItem
        _listDisc.value = discItem.list.onEach { disc ->
            when (disc.addBy) {
                MANUALLY -> disc.isPresentByManually = true
                SCAN -> disc.isPresentByScan = true
                SEARCH -> disc.isPresentBySearch = true
            }
        }.sortedByDescending { disc ->
            disc.isPresentByManually == true
        }
        _addBy.value = discItem.discAdd.addBy
    }

    fun onButtonAddClicked() {
        viewModelScope.launch {
            when (discItem.discAdd.addBy) {
                MANUALLY -> {
                    val id = addDiscDatabase()
                    onNavigateToDisc(
                        snackBarMessage = MyApp.res.getString(R.string.disc_added),
                        id = id
                    )
                }
                else -> onNavigateToDiscResultSearch()
            }
        }
    }

    fun onButtonCancelClicked() {
        onNavigateToDisc(
            snackBarMessage = getSnackBarMessage(),
            id = discItem.list[0].id
        )
    }

    fun onButtonOkClicked() {
        onNavigateToDisc(
            snackBarMessage = getSnackBarMessage(),
            id = discItem.list[0].id
        )
    }

    private fun getSnackBarMessage() =
        when (discItem.list.size) {
            1 -> MyApp.res.getString(R.string.disc_already_present_one)
            else -> MyApp.res.getString(R.string.disc_already_present_more)
        }

    private fun onNavigateToDisc(snackBarMessage: String, id: Long) {
        _navigateToDisc.value = Pair(snackBarMessage, id)
    }

    fun onNavigateToDiscDone() {
        _navigateToDisc.value = null
    }

    private fun onNavigateToDiscResultSearch() {
        discItem.discAdd.addBy = SEARCH
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
                addBy = MANUALLY
            )
        )
}