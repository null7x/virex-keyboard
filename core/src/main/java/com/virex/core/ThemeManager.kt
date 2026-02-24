package com.virex.core

import com.virex.core.model.KeyboardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private val _currentTheme = MutableStateFlow<KeyboardTheme?>(null)
    val currentTheme: StateFlow<KeyboardTheme?> = _currentTheme.asStateFlow()

    fun applyTheme(theme: KeyboardTheme) {
        _currentTheme.value = theme
    }
}
