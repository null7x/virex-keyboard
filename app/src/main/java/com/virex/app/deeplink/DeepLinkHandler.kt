package com.virex.app.deeplink

import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Deep link handler for viral share feature
 * Handles: virexkeyboard://theme/{themeId}
 */
object DeepLinkHandler {
    
    private const val TAG = "DeepLinkHandler"
    private const val SCHEME = "virexkeyboard"
    private const val HOST_THEME = "theme"

    data class DeepLink(
        val type: Type,
        val themeId: String? = null
    ) {
        enum class Type {
            THEME_PREVIEW,
            UNKNOWN
        }
    }

    fun handleIntent(intent: Intent?): DeepLink? {
        if (intent == null) return null
        
        val action = intent.action
        val data = intent.data

        if (action != Intent.ACTION_VIEW || data == null) {
            return null
        }

        return parseDeepLink(data)
    }

    private fun parseDeepLink(uri: Uri): DeepLink? {
        if (uri.scheme != SCHEME) {
            Log.w(TAG, "Invalid scheme: ${uri.scheme}")
            return null
        }

        return when (uri.host) {
            HOST_THEME -> {
                val themeId = uri.pathSegments.firstOrNull()
                if (themeId != null) {
                    Log.d(TAG, "Theme deep link: $themeId")
                    DeepLink(DeepLink.Type.THEME_PREVIEW, themeId)
                } else {
                    Log.w(TAG, "Missing theme ID in deep link")
                    null
                }
            }
            else -> {
                Log.w(TAG, "Unknown deep link host: ${uri.host}")
                DeepLink(DeepLink.Type.UNKNOWN)
            }
        }
    }

    fun createThemeDeepLink(themeId: String): String {
        return "$SCHEME://$HOST_THEME/$themeId"
    }
}
