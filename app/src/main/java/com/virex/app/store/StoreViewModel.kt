package com.virex.app.store

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virex.analytics.AnalyticsBatcher
import com.virex.app.data.AppPreferences
import com.virex.app.data.ThemeRepository
import com.virex.core.RecommendationEngine
import com.virex.core.ThemeBroadcaster
import com.virex.core.ThemeManager
import com.virex.core.model.AnalyticsEvent
import com.virex.core.model.KeyboardTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreState(
    val themes: List<KeyboardTheme> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val selectedThemeId: String? = null,
    val isLoading: Boolean = false,
    val isPro: Boolean = false
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val themeRepository: ThemeRepository,
    private val preferences: AppPreferences,
    private val recommendationEngine: RecommendationEngine,
    private val analyticsBatcher: AnalyticsBatcher,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _state = MutableStateFlow(StoreState())
    val state: StateFlow<StoreState> = _state.asStateFlow()

    private val applyCount = mutableMapOf<String, Int>()
    private val viewTime = mutableMapOf<String, Long>()
    private val categoryAffinity = mutableMapOf<String, Int>()

    init {
        loadThemes()
        observePreferences()
        trackEvent("app_open")
    }

    private fun loadThemes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val cached = themeRepository.loadFromCacheFirst()
            _state.value = _state.value.copy(themes = cached, isLoading = false)
            
            // Silent sync in background
            val fresh = themeRepository.silentSyncFromRemote()
            val ranked = recommendationEngine.rank(fresh, applyCount, viewTime, categoryAffinity)
            _state.value = _state.value.copy(themes = ranked)
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferences.selectedThemeId.collect { themeId ->
                _state.value = _state.value.copy(selectedThemeId = themeId)
            }
        }
        viewModelScope.launch {
            preferences.isProEnabled.collect { isPro ->
                _state.value = _state.value.copy(isPro = isPro)
            }
        }
    }

    fun applyTheme(theme: KeyboardTheme) {
        viewModelScope.launch {
            preferences.setSelectedTheme(theme.id)
            themeManager.applyTheme(theme)
            ThemeBroadcaster.sendThemeChanged(
                context,
                theme.id,
                theme.keyColor,
                theme.fontColor,
                theme.background
            )
            
            applyCount[theme.id] = (applyCount[theme.id] ?: 0) + 1
            categoryAffinity[theme.category] = (categoryAffinity[theme.category] ?: 0) + 1
            
            trackEvent("theme_applied", mapOf("theme_id" to theme.id, "category" to theme.category))
        }
    }

    fun toggleFavorite(themeId: String) {
        val current = _state.value.favorites
        _state.value = _state.value.copy(
            favorites = if (current.contains(themeId)) current - themeId else current + themeId
        )
    }

    fun recordThemeView(themeId: String, durationMs: Long) {
        viewTime[themeId] = (viewTime[themeId] ?: 0L) + durationMs
    }

    fun trackEvent(name: String, payload: Map<String, String> = emptyMap()) {
        analyticsBatcher.track(
            AnalyticsEvent(
                name = name,
                timestamp = System.currentTimeMillis(),
                payload = payload
            )
        )
    }
}
