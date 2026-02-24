package com.virex.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "virex_prefs")

class AppPreferences(private val context: Context) {

    private val selectedThemeKey = stringPreferencesKey("selected_theme")
    private val keyboardEnabledKey = booleanPreferencesKey("keyboard_enabled")
    private val proEnabledKey = booleanPreferencesKey("pro_enabled")

    val selectedThemeId: Flow<String?> = context.dataStore.data.map { it[selectedThemeKey] }
    val isKeyboardEnabled: Flow<Boolean> = context.dataStore.data.map { it[keyboardEnabledKey] ?: false }
    val isProEnabled: Flow<Boolean> = context.dataStore.data.map { it[proEnabledKey] ?: false }

    suspend fun setSelectedTheme(themeId: String) {
        context.dataStore.edit { it[selectedThemeKey] = themeId }
    }

    suspend fun setKeyboardEnabled(enabled: Boolean) {
        context.dataStore.edit { it[keyboardEnabledKey] = enabled }
    }

    suspend fun setProEnabled(enabled: Boolean) {
        context.dataStore.edit { it[proEnabledKey] = enabled }
    }
}
