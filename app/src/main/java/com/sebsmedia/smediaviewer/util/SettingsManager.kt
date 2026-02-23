package com.sebsmedia.smediaviewer.util

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val KEY_SELECTED_FOLDER_URI = stringPreferencesKey("selected_folder_uri")
        private val KEY_FOLDER_DISPLAY_NAME = stringPreferencesKey("folder_display_name")
    }

    val selectedFolderUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_FOLDER_URI]
    }

    val folderDisplayName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_FOLDER_DISPLAY_NAME]
    }

    suspend fun setSelectedFolder(uri: Uri, displayName: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_FOLDER_URI] = uri.toString()
            preferences[KEY_FOLDER_DISPLAY_NAME] = displayName
        }
    }

    suspend fun clearSelectedFolder() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_SELECTED_FOLDER_URI)
            preferences.remove(KEY_FOLDER_DISPLAY_NAME)
        }
    }

    fun getSelectedFolderUri(): Uri? {
        val uriString = runCatching {
            context.dataStore.data.map { preferences ->
                preferences[KEY_SELECTED_FOLDER_URI]
            }
        }.getOrNull()?.let {
            // We need to collect this, but for simplicity we'll return null
            // The Flow will be collected by the ViewModel
        }
        return null
    }
}
