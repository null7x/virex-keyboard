package com.virex.core.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
 * RGB animated effect configuration
 */
@Keep
@JsonClass(generateAdapter = true)
data class RgbEffect(
    val type: RgbEffectType,
    val speed: Float = 1.0f, // 0.5 = slow, 1.0 = normal, 2.0 = fast
    val intensity: Float = 1.0f, // 0.0 to 1.0
    val direction: RgbDirection = RgbDirection.HORIZONTAL,
    val colors: List<String> = listOf("#FF0000", "#00FF00", "#0000FF"), // RGB gradient colors
    val waveLength: Float = 1.0f // For wave effects
)

enum class RgbEffectType {
    NONE,
    RAINBOW_WAVE,      // Moving rainbow wave across keyboard
    BREATHING,         // Pulsing brightness effect
    RIPPLE,            // Ripple from touch point
    CYCLE,             // Cycling through colors
    GRADIENT_SHIFT,    // Shifting gradient
    SPARKLE,           // Random sparkles
    BORDER_GLOW        // Glowing border around keys
}

enum class RgbDirection {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
    RADIAL
}

/**
 * Extended KeyboardTheme with RGB support
 */
fun KeyboardTheme.hasRgbEffect(): Boolean {
    return type == "animated" && effects.containsKey("rgb")
}
