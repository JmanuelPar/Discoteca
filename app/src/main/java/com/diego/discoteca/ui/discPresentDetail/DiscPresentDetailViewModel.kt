package com.diego.discoteca.ui.discPresentDetail

import androidx.lifecycle.*
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.AddBy
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DiscPresentDetailViewModel(
    val repository: DiscsRepository,
    val discPresent: DiscPresent
) : ViewModel() {

    private val discDatabase: LiveData<Disc> = repository.getDiscWithId(discPresent.discAdd.id)
    fun getDisc() = discDatabase

    private val _discAddBy = MutableLiveData<AddBy>()
    private val discAddBy: LiveData<AddBy>
        get() = _discAddBy

    val isVisible = Transformations.map(discDatabase) { disc ->
        disc.addBy == AddBy.MANUALLY
    }.asFlow()
        .combine(Transformations.map(discAddBy) { addBy ->
            addBy == AddBy.SEARCH
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
        discPresent.discAdd.addBy = AddBy.MANUALLY
        _navigateToDiscResultSearch.value = discPresent
    }

    fun onNavigateToDiscResultSearchDone() {
        _navigateToDiscResultSearch.value = null
    }
}