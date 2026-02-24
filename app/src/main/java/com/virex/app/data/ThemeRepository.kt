package com.virex.app.data

import com.virex.core.model.KeyboardTheme
import kotlinx.coroutines.delay

class ThemeRepository {

    private var cache: List<KeyboardTheme> = emptyList()

    suspend fun loadFromCacheFirst(): List<KeyboardTheme> {
        if (cache.isNotEmpty()) return cache
        cache = seedThemes()
        return cache
    }

    suspend fun silentSyncFromRemote(): List<KeyboardTheme> {
        delay(300) // simulate network sync in background
        cache = seedThemes().shuffled()
        return cache
    }

    private fun seedThemes() = listOf(
        KeyboardTheme("neon_wave", "Neon Wave", "trending", "#0B0D22", "#3F51FF", "#FFFFFF", false),
        KeyboardTheme("amoled_lava", "AMOLED Lava", "new", "#050505", "#FF2D55", "#FFFFFF", true),
        KeyboardTheme("cyber_ice", "Cyber Ice", "for_you", "#0A0F1E", "#11E0FF", "#D9F9FF", false)
    )
}
