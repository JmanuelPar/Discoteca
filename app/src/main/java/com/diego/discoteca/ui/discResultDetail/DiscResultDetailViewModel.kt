package com.diego.discoteca.ui.discResultDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.data.model.DiscLight
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.UIText
import kotlinx.coroutines.launch

class DiscResultDetailViewModel(
    val repository: DiscsRepository,
    private val discItem: DiscResultDetail,
) : ViewModel() {

    private val discChosen = discItem.disc

    private val _discResultDetail = MutableLiveData<DiscResultDetail>()
    val discResultDetail: LiveData<DiscResultDetail>
        get() = _discResultDetail

    private val _navigateToDisc = MutableLiveData<Pair<UIText, Long>?>()
    val navigateToDisc: LiveData<Pair<UIText, Long>?>
        get() = _navigateToDisc

    private val _navigatePopStack = MutableLiveData<Boolean>()
    val navigatePopStack: LiveData<Boolean>
        get() = _navigatePopStack

    init {
        _discResultDetail.value = discItem
    }

    fun onButtonYesClicked() {
        viewModelScope.launch {
            when (discItem.addBy) {
                AddBy.SCAN -> {
                    when {
                        // Button update -> update this disc
                        discChosen.isPresentByManually == true
                                || discChosen.isPresentBySearch == true -> {
                            updateDisc(false)
                            onNavigateToDisc(
                                uiText = UIText.DiscUpdated,
                                id = discChosen.discLight!!.id
                            )
                        }
                        // Button ok -> popStack
                        discChosen.isPresentByScan == true -> onNavigatePopStack()
                        else -> {
                            // Button add -> add this disc in database, keep the barcode
                            val id = addDiscDatabase(false)
                            onNavigateToDisc(
                                uiText = UIText.DiscAdded,
                                id = id
                            )
                        }
                    }
                }
                // AddBy.SEARCH
                else -> {
                    when {
                        // Button update -> update this disc
                        discChosen.isPresentByManually == true -> {
                            updateDisc(true)
                            onNavigateToDisc(
                                uiText = UIText.DiscUpdated,
                                id = discChosen.discLight!!.id
                            )
                        }
                        // Button ok -> popStack
                        discChosen.isPresentByScan == true
                                || discChosen.isPresentBySearch == true -> onNavigatePopStack()

                        else -> {
                            // Button add -> add this disc in database, don't keep the barcode
                            val id = addDiscDatabase(true)
                            onNavigateToDisc(
                                uiText = UIText.DiscAdded,
                                id = id
                            )
                        }
                    }
                }
            }
        }
    }

    fun onButtonNoClicked() {
        onNavigatePopStack()
    }

    private fun onNavigateToDisc(uiText: UIText, id: Long) {
        _navigateToDisc.value = Pair(uiText, id)
    }

    fun onNavigateToDiscDone() {
        _navigateToDisc.value = null
    }

    private fun onNavigatePopStack() {
        _navigatePopStack.value = true
    }

    fun onNavigatePopStackDone() {
        _navigatePopStack.value = false
    }

    private suspend fun addDiscDatabase(empty: Boolean): Long {
        discChosen.addBy = discItem.addBy
        if (empty) discChosen.barcode = ""
        return repository.insertLong(discChosen)
    }

    private suspend fun updateDisc(search: Boolean) {
        val discUpdate = Disc(
            id = discChosen.discLight!!.id,
            name = discChosen.name,
            title = discChosen.title,
            year = discChosen.year,
            country = discChosen.country,
            format = discChosen.format,
            formatMedia = discChosen.formatMedia,
            coverImage = discChosen.coverImage,
            barcode = if (search) "" else discChosen.barcode,
            idDisc = discChosen.idDisc,
            addBy = discItem.addBy,
            discLight = DiscLight(
                id = 0L,
                name = "",
                title = "",
                year = "",
                country = "",
                format = "",
                formatMedia = ""
            )
        )

        repository.update(discUpdate)
    }
}