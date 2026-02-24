package com.virex.app.pro

import com.virex.core.model.KeyboardFont
import com.virex.core.model.RgbEffect

/**
 * PRO Feature gate
 * Checks if user has access to PRO features
 */
object ProGate {

    /**
     * Check if theme is accessible by user
     */
    fun canAccessTheme(themeIsPro: Boolean, userIsPro: Boolean): Boolean {
        return !themeIsPro || userIsPro
    }

    /**
     * Check if font is accessible by user
     */
    fun canAccessFont(font: KeyboardFont, userIsPro: Boolean): Boolean {
        return !font.isPro || userIsPro
    }

    /**
     * Check if RGB effects are accessible
     */
    fun canUseRgbEffects(userIsPro: Boolean): Boolean {
        return userIsPro
    }

    /**
     * Check if custom fonts are accessible
     */
    fun canUseCustomFonts(userIsPro: Boolean): Boolean {
        return userIsPro
    }

    /**
     * Get paywall message for locked feature
     */
    fun getPaywallMessage(feature: ProFeature): String {
        return when (feature) {
            ProFeature.PRO_THEME -> "Unlock PRO to use exclusive themes"
            ProFeature.RGB_EFFECTS -> "Upgrade to PRO for RGB animations"
            ProFeature.CUSTOM_FONTS -> "Get PRO to customize keyboard fonts"
            ProFeature.NO_ADS -> "Remove ads with PRO subscription"
        }
    }
}

enum class ProFeature {
    PRO_THEME,
    RGB_EFFECTS,
    CUSTOM_FONTS,
    NO_ADS
}
