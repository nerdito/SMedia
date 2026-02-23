package com.sebsmedia.smediaviewer.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurityManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_PIN = "pin_code"
        private const val KEY_PIN_SET = "pin_set"
    }

    fun savePin(pin: String) {
        securePrefs.edit().apply {
            putString(KEY_PIN, pin)
            putBoolean(KEY_PIN_SET, true)
            apply()
        }
    }

    fun verifyPin(pin: String): Boolean {
        val savedPin = securePrefs.getString(KEY_PIN, null)
        return savedPin == pin
    }

    fun isPinSet(): Boolean {
        return securePrefs.getBoolean(KEY_PIN_SET, false)
    }

    fun clearPin() {
        securePrefs.edit().apply {
            remove(KEY_PIN)
            putBoolean(KEY_PIN_SET, false)
            apply()
        }
    }
}
