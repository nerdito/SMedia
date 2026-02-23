package com.sebsmedia.smediaviewer.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sebsmedia.smediaviewer.data.model.VideoFile
import com.sebsmedia.smediaviewer.data.repository.MediaRepository
import com.sebsmedia.smediaviewer.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)
    private val settingsManager = SettingsManager(application)

    private val _videos = MutableStateFlow<List<VideoFile>>(emptyList())
    val videos: StateFlow<List<VideoFile>> = _videos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedVideo = MutableStateFlow<VideoFile?>(null)
    val selectedVideo: StateFlow<VideoFile?> = _selectedVideo.asStateFlow()

    private val _selectedFolderUri = MutableStateFlow<Uri?>(null)
    val selectedFolderUri: StateFlow<Uri?> = _selectedFolderUri.asStateFlow()

    init {
        loadSelectedFolder()
        loadVideos()
    }

    private fun loadSelectedFolder() {
        viewModelScope.launch {
            settingsManager.selectedFolderUri.collect { uriString ->
                _selectedFolderUri.value = uriString?.let { Uri.parse(it) }
            }
        }
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val folderUri = settingsManager.selectedFolderUri.first()
                val uri = folderUri?.let { Uri.parse(it) }
                _videos.value = repository.getVideosFromFolder(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                _videos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectVideo(video: VideoFile) {
        _selectedVideo.value = video
    }

    fun clearSelectedVideo() {
        _selectedVideo.value = null
    }
}
