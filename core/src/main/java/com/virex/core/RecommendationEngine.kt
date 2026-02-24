package com.virex.core

import com.virex.core.model.KeyboardTheme

class RecommendationEngine {
    fun rank(
        themes: List<KeyboardTheme>,
        applyCount: Map<String, Int>,
        viewTime: Map<String, Long>,
        categoryAffinity: Map<String, Int>
    ): List<KeyboardTheme> {
        return themes.sortedByDescending { theme ->
            (applyCount[theme.id] ?: 0) * 10 +
                (viewTime[theme.id] ?: 0L).toInt() +
                (categoryAffinity[theme.category] ?: 0) * 5
        }
    }
}
