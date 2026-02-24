package com.virex.core.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
 * Custom font for keyboard
 */
@Keep
@JsonClass(generateAdapter = true)
data class KeyboardFont(
    val id: String,
    val name: String,
    val displayName: String,
    val previewUrl: String,
    val fontUrl: String, // Vercel Blob URL for .ttf/.otf
    val isPro: Boolean = false,
    val category: FontCategory = FontCategory.SANS_SERIF,
    val fileSize: Long = 0, // in bytes
    val popularity: Int = 0
)

enum class FontCategory {
    SANS_SERIF,
    SERIF,
    MONOSPACE,
    HANDWRITING,
    DISPLAY
}

/**
 * Font download state
 */
sealed class FontState {
    object NotDownloaded : FontState()
    data class Downloading(val progress: Float) : FontState()
    object Downloaded : FontState()
    data class Error(val message: String) : FontState()
}
