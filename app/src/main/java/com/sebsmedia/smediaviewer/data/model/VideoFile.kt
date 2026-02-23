package com.sebsmedia.smediaviewer.data.model

import android.net.Uri

data class VideoFile(
    val id: Long,
    val title: String,
    val uri: Uri,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val mimeType: String?
)
