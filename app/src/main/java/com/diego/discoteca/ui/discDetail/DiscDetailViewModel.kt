package com.diego.discoteca.ui.discDetail

import androidx.lifecycle.*
import com.diego.discoteca.domain.Disc
import com.diego.discoteca.repository.DiscRepository

class DiscDetailViewModel(
    val repository: DiscRepository,
    discKey: Long = 0L
) : ViewModel() {

    private val disc: LiveData<Disc> = repository.getDiscWithId(discKey)
    fun getDisc() = disc

    private val _buttonOk = MutableLiveData<Boolean>()
    val buttonOk: LiveData<Boolean>
        get() = _buttonOk

    fun buttonOkClicked() {
        _buttonOk.value = true
    }

    fun buttonOkClickedDone() {
        _buttonOk.value = false
    }
}