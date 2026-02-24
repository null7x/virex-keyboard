package com.virex.app.fonts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virex.billing.BillingGateway
import com.virex.core.font.FontManager
import com.virex.core.model.FontState
import com.virex.core.model.KeyboardFont
import com.virex.core.network.ThemeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val fontManager: FontManager,
    private val themeApi: ThemeApi,
    private val billingGateway: BillingGateway
) : ViewModel() {

    private val _fonts = MutableStateFlow<List<KeyboardFont>>(emptyList())
    val fonts: StateFlow<List<KeyboardFont>> = _fonts.asStateFlow()

    val fontStates: StateFlow<Map<String, FontState>> = fontManager.fontStates
    val currentFontId: StateFlow<String> = fontManager.currentFontId
    val isPro: StateFlow<Boolean> = billingGateway.isProActive

    /**
     * Load fonts from backend
     */
    fun loadFonts() {
        viewModelScope.launch {
            try {
                val fontsList = themeApi.fonts()
                _fonts.value = fontsList.sortedByDescending { it.popularity }
                
                // Initialize font states
                fontManager.initializeFontStates(fontsList)
            } catch (e: Exception) {
                // Fallback to empty list or cached fonts
                _fonts.value = emptyList()
            }
        }
    }

    /**
     * Download font
     */
    fun downloadFont(font: KeyboardFont) {
        viewModelScope.launch {
            fontManager.downloadFont(font)
        }
    }

    /**
     * Apply font to keyboard
     */
    fun applyFont(fontId: String) {
        fontManager.setCurrentFont(fontId)
        // Font will be applied to keyboard service via broadcast or shared preferences
        // TODO: Broadcast font change to VirexKeyboardService
    }

    /**
     * Delete font
     */
    fun deleteFont(fontId: String) {
        viewModelScope.launch {
            fontManager.deleteFont(fontId)
        }
    }
}
