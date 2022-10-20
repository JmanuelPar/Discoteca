package com.diego.discoteca.ui.discDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.repository.DiscsRepository

class DiscDetailViewModel(
    val repository: DiscsRepository,
    discId: Long = 0L
) : ViewModel() {

    private val disc: LiveData<Disc> = repository.getDiscWithId(discId)
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