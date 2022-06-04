package com.diego.discoteca.ui.discPresentDetail

import androidx.lifecycle.*
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.model.*
import com.diego.discoteca.repository.DiscRepository
import com.diego.discoteca.util.MANUALLY
import com.diego.discoteca.util.SEARCH
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DiscPresentDetailViewModel(
    val repository: DiscRepository,
    val discPresent: DiscPresent
) : ViewModel() {

    private val discDatabase: LiveData<Disc> = repository.getDiscWithId(discPresent.discAdd.id)
    fun getDisc() = discDatabase

    private val _discAddBy = MutableLiveData<Int>()
    private val discAddBy: LiveData<Int>
        get() = _discAddBy

    val isVisible = Transformations.map(discDatabase) { disc ->
        disc.addBy == MANUALLY
    }.asFlow()
        .combine(Transformations.map(discAddBy) { addBy ->
            addBy == SEARCH
        }.asFlow()) { isManually, isSearch ->
            isManually == true && isSearch == true
        }.asLiveData()

    private val _navigatePopStack = MutableLiveData<Boolean>()
    val navigatePopStack: LiveData<Boolean>
        get() = _navigatePopStack

    private val _navigateToDiscResultSearch = MutableLiveData<DiscPresent?>()
    val navigateToDiscResultSearch: LiveData<DiscPresent?>
        get() = _navigateToDiscResultSearch

    init {
        _discAddBy.value = discPresent.discAdd.addBy
    }

    // Button search
    fun onButtonSearchClicked() {
        // Make a search in Discogs Api
        viewModelScope.launch {
            onNavigateToDiscResultSearch()
        }
    }

    // Button ok / cancel
    fun onButtonCancelOkClicked() {
        onNavigatePopStack()
    }

    private fun onNavigatePopStack() {
        _navigatePopStack.value = true
    }

    fun onNavigatePopStackDone() {
        _navigatePopStack.value = false
    }

    private fun onNavigateToDiscResultSearch() {
        discPresent.discAdd.addBy = MANUALLY
        _navigateToDiscResultSearch.value = discPresent
    }

    fun onNavigateToDiscResultSearchDone() {
        _navigateToDiscResultSearch.value = null
    }
}