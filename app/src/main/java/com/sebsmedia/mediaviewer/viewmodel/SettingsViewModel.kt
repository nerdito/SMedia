package com.sebsmedia.mediaviewer.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sebsmedia.mediaviewer.util.SecurityManager
import com.sebsmedia.mediaviewer.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val securityManager = SecurityManager(application)
    private val settingsManager = SettingsManager(application)

    private val _selectedFolderUri = MutableStateFlow<Uri?>(null)
    val selectedFolderUri: StateFlow<Uri?> = _selectedFolderUri.asStateFlow()

    private val _folderDisplayName = MutableStateFlow<String?>(null)
    val folderDisplayName: StateFlow<String?> = _folderDisplayName.asStateFlow()

    private val _isPinSet = MutableStateFlow(false)
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    private val _pinVerificationResult = MutableStateFlow<PinVerificationResult?>(null)
    val pinVerificationResult: StateFlow<PinVerificationResult?> = _pinVerificationResult.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _isPinSet.value = securityManager.isPinSet()

            settingsManager.selectedFolderUri.collect { uriString ->
                _selectedFolderUri.value = uriString?.let { Uri.parse(it) }
            }
        }

        viewModelScope.launch {
            settingsManager.folderDisplayName.collect { name ->
                _folderDisplayName.value = name
            }
        }
    }

    fun setSelectedFolder(uri: Uri, displayName: String) {
        viewModelScope.launch {
            settingsManager.setSelectedFolder(uri, displayName)
            _selectedFolderUri.value = uri
            _folderDisplayName.value = displayName
        }
    }

    fun verifyPin(pin: String): Boolean {
        val isValid = securityManager.verifyPin(pin)
        _pinVerificationResult.value = if (isValid) {
            PinVerificationResult.Success
        } else {
            PinVerificationResult.Error("Invalid PIN")
        }
        return isValid
    }

    fun savePin(pin: String) {
        securityManager.savePin(pin)
        _isPinSet.value = true
    }

    fun clearPinVerificationResult() {
        _pinVerificationResult.value = null
    }

    fun clearPin() {
        securityManager.clearPin()
        _isPinSet.value = false
    }
}

sealed class PinVerificationResult {
    object Success : PinVerificationResult()
    data class Error(val message: String) : PinVerificationResult()
}
