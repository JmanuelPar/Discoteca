package com.diego.discoteca.ui.interaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.UIText
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class InteractionViewModel(val repository: DiscsRepository) : ViewModel() {

    val numberDiscs = repository.countAllDiscs.asLiveData()

    private val _isSignIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean>
        get() = _isSignIn

    private val _googleAccount = MutableLiveData<UIText?>()
    val googleAccount: LiveData<UIText?>
        get() = _googleAccount

    private val _driveLogOutClicked = MutableLiveData<Boolean>()
    val driveLogOutClicked: LiveData<Boolean>
        get() = _driveLogOutClicked

    private val _driveDisconnectClicked = MutableLiveData<Boolean>()
    val driveDisconnectClicked: LiveData<Boolean>
        get() = _driveDisconnectClicked

    private val _visibilitySignInButton = MutableLiveData<Boolean>()
    val visibilitySignInButton: LiveData<Boolean>
        get() = _visibilitySignInButton

    private val _visibilityDriveLogOutButton = MutableLiveData<Boolean>()
    val visibilityDriveLogOutButton: LiveData<Boolean>
        get() = _visibilityDriveLogOutButton

    private val _visibilityDriveDisconnectButton = MutableLiveData<Boolean>()
    val visibilityDriveDisconnectButton: LiveData<Boolean>
        get() = _visibilityDriveDisconnectButton

    private val _stateIconGDriveUpload = MutableLiveData(false)
    val stateIconGDriveUpload: LiveData<Boolean>
        get() = _stateIconGDriveUpload

    private val _stateIconGDriveDownload = MutableLiveData(false)
    val stateIconGDriveDownload: LiveData<Boolean>
        get() = _stateIconGDriveDownload

    private val _uploadTime = MutableLiveData("")
    val uploadTime: LiveData<String>
        get() = _uploadTime

    private val _downloadTime = MutableLiveData("")
    val downloadTime: LiveData<String>
        get() = _downloadTime

    private val _progressLinearGDriveUpload = MutableLiveData<Boolean>()
    val progressLinearGDriveUpload: LiveData<Boolean>
        get() = _progressLinearGDriveUpload

    private val _progressLinearGDriveDownload = MutableLiveData<Boolean>()
    val progressLinearGDriveDownload: LiveData<Boolean>
        get() = _progressLinearGDriveDownload

    private val _iconGDriveUploadClicked = MutableLiveData<Boolean>()
    val iconGDriveUploadClicked: LiveData<Boolean>
        get() = _iconGDriveUploadClicked

    private val _iconGDriveDownloadClicked = MutableLiveData<Boolean>()
    val iconGDriveDownloadClicked: LiveData<Boolean>
        get() = _iconGDriveDownloadClicked

    init {
        updateProgressLinearGDriveUpload(false)
        updateProgressLinearGDriveDownload(false)
    }

    fun updateUI(account: GoogleSignInAccount?) {
        when {
            account != null -> {
                // Log in in Drive
                displayAccount(
                    uiText = UIText.AccountLogIn("${account.displayName}\n${account.email}"),
                    visibility = true
                )
                updateIconGDriveUpload(true)
            }
            else -> {
                // Not log in in Drive
                displayAccount(
                    uiText = UIText.AccountNotLogIn,
                    visibility = false
                )
                updateIconGDriveAndTime()
            }
        }
    }

    private fun displayAccount(uiText: UIText, visibility: Boolean) {
        _googleAccount.value = uiText
        _visibilitySignInButton.value = !visibility
        _visibilityDriveLogOutButton.value = visibility
        _visibilityDriveDisconnectButton.value = visibility
        _isSignIn.value = visibility
    }

    private fun updateIconGDriveAndTime() {
        updateIconGDriveUpload(false)
        updateIconGDriveDownload(false)
        _uploadTime.value = ""
        _downloadTime.value = ""
    }

    fun driveLogOutClicked() {
        _driveLogOutClicked.value = true
    }

    fun driveLogOutClickedDone() {
        _driveLogOutClicked.value = false
    }

    fun driveDisconnectClicked() {
        _driveDisconnectClicked.value = true
    }

    fun driveDisconnectClickedDone() {
        _driveDisconnectClicked.value = false
    }

    fun updateIconGDriveUpload(enabled: Boolean) {
        _stateIconGDriveUpload.value = enabled
    }

    fun updateIconGDriveDownload(enabled: Boolean) {
        _stateIconGDriveDownload.value = enabled
    }

    fun updateUploadTime(time: String) {
        _uploadTime.value = time
    }

    fun updateDownloadTime(time: String) {
        _downloadTime.value = time
    }

    fun updateProgressLinearGDriveUpload(visibility: Boolean) {
        _progressLinearGDriveUpload.value = visibility
    }

    fun updateProgressLinearGDriveDownload(visibility: Boolean) {
        _progressLinearGDriveDownload.value = visibility
    }

    fun iconGDriveUploadClicked() {
        _iconGDriveUploadClicked.value = true
    }

    fun iconGDriveUploadClickedDone() {
        _iconGDriveUploadClicked.value = false
    }

    fun iconGDriveDownloadClicked() {
        _iconGDriveDownloadClicked.value = true
    }

    fun iconGDriveDownloadClickedDone() {
        _iconGDriveDownloadClicked.value = false
    }
}