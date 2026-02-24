package com.virex.core.rgb

import android.graphics.Color
import android.os.SystemClock
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * RGB Animation Engine
 * Calculates animated RGB colors for keyboard keys at 60 FPS
 */
class RgbAnimator {

    companion object {
        private const val TWO_PI = Math.PI * 2
    }

    private var startTime = SystemClock.uptimeMillis()
    
    /**
     * Reset animation timer
     */
    fun reset() {
        startTime = SystemClock.uptimeMillis()
    }

    /**
     * Get elapsed time in seconds
     */
    private fun getElapsedSeconds(): Float {
        return (SystemClock.uptimeMillis() - startTime) / 1000f
    }

    /**
     * Calculate rainbow wave color
     * Creates a moving rainbow gradient across the keyboard
     */
    fun calculateRainbowWave(
        normalizedX: Float, // 0.0 to 1.0 (position in keyboard)
        normalizedY: Float,
        speed: Float,
        waveLength: Float
    ): Int {
        val time = getElapsedSeconds() * speed
        val hue = ((normalizedX * waveLength + time) % 1.0f) * 360f
        return Color.HSVToColor(floatArrayOf(hue, 1.0f, 1.0f))
    }

    /**
     * Calculate breathing effect
     * Pulsing brightness on/off
     */
    fun calculateBreathing(
        baseColor: Int,
        speed: Float,
        intensity: Float
    ): Int {
        val time = getElapsedSeconds() * speed
        val brightness = (sin(time * TWO_PI).toFloat() + 1f) / 2f * intensity
        
        val r = Color.red(baseColor)
        val g = Color.green(baseColor)
        val b = Color.blue(baseColor)
        
        return Color.rgb(
            (r * brightness).toInt().coerceIn(0, 255),
            (g * brightness).toInt().coerceIn(0, 255),
            (b * brightness).toInt().coerceIn(0, 255)
        )
    }

    /**
     * Calculate color cycle effect
     * Cycles through a list of colors
     */
    fun calculateCycle(
        colors: List<Int>,
        speed: Float
    ): Int {
        if (colors.isEmpty()) return Color.WHITE
        
        val time = getElapsedSeconds() * speed
        val index = (time % colors.size.toFloat()).toInt()
        val nextIndex = (index + 1) % colors.size
        val progress = time % 1.0f
        
        return interpolateColor(colors[index], colors[nextIndex], progress)
    }

    /**
     * Calculate gradient shift effect
     * Smoothly shifts between gradient colors
     */
    fun calculateGradientShift(
        normalizedPosition: Float, // 0.0 to 1.0
        colors: List<Int>,
        speed: Float
    ): Int {
        if (colors.isEmpty()) return Color.WHITE
        if (colors.size == 1) return colors[0]
        
        val time = getElapsedSeconds() * speed
        val offset = (time % 1.0f)
        val adjustedPosition = (normalizedPosition + offset) % 1.0f
        
        val segmentSize = 1.0f / (colors.size - 1)
        val segmentIndex = (adjustedPosition / segmentSize).toInt().coerceIn(0, colors.size - 2)
        val segmentProgress = (adjustedPosition % segmentSize) / segmentSize
        
        return interpolateColor(colors[segmentIndex], colors[segmentIndex + 1], segmentProgress)
    }

    /**
     * Calculate ripple effect from touch point
     */
    fun calculateRipple(
        normalizedX: Float,
        normalizedY: Float,
        rippleX: Float,
        rippleY: Float,
        rippleTime: Float, // Time since ripple started
        color: Int,
        speed: Float
    ): Int? {
        val distance = kotlin.math.sqrt(
            (normalizedX - rippleX) * (normalizedX - rippleX) +
            (normalizedY - rippleY) * (normalizedY - rippleY)
        )
        
        val wavePosition = rippleTime * speed
        val waveDiff = abs(distance - wavePosition)
        
        return if (waveDiff < 0.1f) {
            val alpha = (1f - waveDiff / 0.1f) * 255
            Color.argb(alpha.toInt(), Color.red(color), Color.green(color), Color.blue(color))
        } else {
            null
        }
    }

    /**
     * Calculate sparkle effect
     * Random sparkles across keyboard
     */
    fun calculateSparkle(
        normalizedX: Float,
        normalizedY: Float,
        baseColor: Int,
        intensity: Float
    ): Int {
        val time = getElapsedSeconds()
        val hash = (normalizedX * 1000 + normalizedY * 100 + time * 10).toInt()
        val random = (hash * 2654435761L % 2147483647L) / 2147483647.0
        
        return if (random < intensity * 0.01) {
            Color.WHITE
        } else {
            baseColor
        }
    }

    /**
     * Calculate border glow effect
     */
    fun calculateBorderGlow(
        normalizedX: Float,
        normalizedY: Float,
        speed: Float,
        colors: List<Int>
    ): Int {
        val time = getElapsedSeconds() * speed
        
        // Distance from center
        val distanceFromCenter = kotlin.math.sqrt(
            (normalizedX - 0.5f) * (normalizedX - 0.5f) +
            (normalizedY - 0.5f) * (normalizedY - 0.5f)
        )
        
        // Angle for color selection
        val angle = kotlin.math.atan2(normalizedY - 0.5, normalizedX - 0.5)
        val hue = ((angle / TWO_PI + 0.5 + time) % 1.0).toFloat() * 360f
        
        val brightness = if (distanceFromCenter > 0.4f) 1.0f else distanceFromCenter / 0.4f
        
        return Color.HSVToColor(floatArrayOf(hue, 1.0f, brightness))
    }

    /**
     * Interpolate between two colors
     */
    private fun interpolateColor(color1: Int, color2: Int, progress: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        
        val r = (r1 + (r2 - r1) * progress).toInt().coerceIn(0, 255)
        val g = (g1 + (g2 - g1) * progress).toInt().coerceIn(0, 255)
        val b = (b1 + (b2 - b1) * progress).toInt().coerceIn(0, 255)
        
        return Color.rgb(r, g, b)
    }

    /**
     * Parse hex color string to Int
     */
    fun parseColor(hexColor: String): Int {
        return try {
            Color.parseColor(hexColor)
        } catch (e: Exception) {
            Color.WHITE
        }
    }
}
