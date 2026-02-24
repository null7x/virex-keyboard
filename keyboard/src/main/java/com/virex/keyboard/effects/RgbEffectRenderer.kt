package com.virex.keyboard.effects

import android.graphics.*
import com.virex.core.model.RgbEffect
import com.virex.core.model.RgbEffectType
import com.virex.core.model.RgbDirection
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * RgbEffectRenderer - Renders RGB animated effects on keyboard
 * 
 * Performance target: 60 FPS
 * - Uses lightweight Canvas operations
 * - Optimized paint reuse
 * - Efficient color calculations
 */
class RgbEffectRenderer {
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
    }
    
    // Animation state
    private var animationTime = 0f
    private val colorCache = mutableListOf<Int>()
    
    // Last touch position for ripple effect
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var rippleTime = 0f
    
    /**
     * Update animation frame (call every 16ms for 60 FPS)
     */
    fun update(deltaTimeMs: Long) {
        animationTime += deltaTimeMs / 1000f
        
        // Update ripple
        if (rippleTime > 0) {
            rippleTime += deltaTimeMs / 1000f
            if (rippleTime > 1.5f) {
                rippleTime = 0f
            }
        }
    }
    
    /**
     * Trigger ripple effect at touch position
     */
    fun triggerRipple(x: Float, y: Float) {
        lastTouchX = x
        lastTouchY = y
        rippleTime = 0.01f // Start ripple
    }
    
    /**
     * Draw RGB effect on canvas
     */
    fun draw(
        canvas: Canvas,
        effect: RgbEffect,
        width: Float,
        height: Float,
        keyRects: List<RectF>
    ) {
        when (effect.type) {
            RgbEffectType.NONE -> {
                // No effect
            }
            
            RgbEffectType.RAINBOW_WAVE -> {
                drawRainbowWave(canvas, effect, width, height, keyRects)
            }
            
            RgbEffectType.BREATHING -> {
                drawBreathing(canvas, effect, keyRects)
            }
            
            RgbEffectType.RIPPLE -> {
                if (rippleTime > 0) {
                    drawRipple(canvas, effect, keyRects)
                }
            }
            
            RgbEffectType.CYCLE -> {
                drawCycle(canvas, effect, keyRects)
            }
            
            RgbEffectType.GRADIENT_SHIFT -> {
                drawGradientShift(canvas, effect, width, height)
            }
            
            RgbEffectType.SPARKLE -> {
                drawSparkle(canvas, effect, keyRects)
            }
            
            RgbEffectType.BORDER_GLOW -> {
                drawBorderGlow(canvas, effect, keyRects)
            }
        }
    }
    
    /**
     * Rainbow wave effect - moving rainbow across keyboard
     */
    private fun drawRainbowWave(
        canvas: Canvas,
        effect: RgbEffect,
        width: Float,
        height: Float,
        keyRects: List<RectF>
    ) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        val wavelength = effect.waveLength
        
        keyRects.forEach { rect ->
            val centerX = rect.centerX()
            val centerY = rect.centerY()
            
            // Calculate position in wave (0 to 1)
            val position = when (effect.direction) {
                RgbDirection.HORIZONTAL -> centerX / width
                RgbDirection.VERTICAL -> centerY / height
                RgbDirection.DIAGONAL -> (centerX / width + centerY / height) / 2f
                RgbDirection.RADIAL -> {
                    val dx = centerX - width / 2f
                    val dy = centerY - height / 2f
                    sqrt(dx * dx + dy * dy) / (width / 2f)
                }
            }
            
            // Add time offset for animation
            val phase = (position / wavelength + animationTime * speed) % 1f
            val color = interpolateColors(colors, phase)
            
            // Draw overlay with transparency
            paint.color = color
            paint.alpha = (effect.intensity * 128).toInt()
            canvas.drawRoundRect(rect, 8f, 8f, paint)
        }
    }
    
    /**
     * Breathing effect - pulsing brightness
     */
    private fun drawBreathing(canvas: Canvas, effect: RgbEffect, keyRects: List<RectF>) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        
        // Calculate breathing value (0 to 1)
        val breathe = (sin(animationTime * speed * 2f) + 1f) / 2f
        val alpha = (breathe * effect.intensity * 128).toInt()
        
        paint.color = colors[0]
        paint.alpha = alpha
        
        keyRects.forEach { rect ->
            canvas.drawRoundRect(rect, 8f, 8f, paint)
        }
    }
    
    /**
     * Ripple effect from touch point
     */
    private fun drawRipple(canvas: Canvas, effect: RgbEffect, keyRects: List<RectF>) {
        val colors = parseColors(effect.colors)
        val rippleRadius = rippleTime * 500f * effect.speed
        val maxAlpha = (effect.intensity * 200).toInt()
        
        keyRects.forEach { rect ->
            val dx = rect.centerX() - lastTouchX
            val dy = rect.centerY() - lastTouchY
            val distance = sqrt(dx * dx + dy * dy)
            
            // Check if key is in ripple range
            if (distance < rippleRadius && distance > rippleRadius - 100f) {
                val fade = 1f - rippleTime / 1.5f
                val alpha = (fade * maxAlpha).toInt()
                
                paint.color = colors[0]
                paint.alpha = alpha
                canvas.drawRoundRect(rect, 8f, 8f, paint)
            }
        }
    }
    
    /**
     * Cycle effect - cycling through colors
     */
    private fun drawCycle(canvas: Canvas, effect: RgbEffect, keyRects: List<RectF>) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        val phase = (animationTime * speed * 0.5f) % 1f
        val color = interpolateColors(colors, phase)
        
        paint.color = color
        paint.alpha = (effect.intensity * 128).toInt()
        
        keyRects.forEach { rect ->
            canvas.drawRoundRect(rect, 8f, 8f, paint)
        }
    }
    
    /**
     * Gradient shift - shifting gradient background
     */
    private fun drawGradientShift(
        canvas: Canvas,
        effect: RgbEffect,
        width: Float,
        height: Float
    ) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        
        // Calculate shifting offset
        val offset = (animationTime * speed * 0.3f) % 2f
        
        val shader = when (effect.direction) {
            RgbDirection.HORIZONTAL -> {
                LinearGradient(
                    -width * offset, 0f,
                    width * (1f - offset), 0f,
                    colors.toIntArray(),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            RgbDirection.VERTICAL -> {
                LinearGradient(
                    0f, -height * offset,
                    0f, height * (1f - offset),
                    colors.toIntArray(),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            else -> {
                LinearGradient(
                    0f, 0f,
                    width, height,
                    colors.toIntArray(),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        }
        
        paint.shader = shader
        paint.alpha = (effect.intensity * 64).toInt()
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.shader = null
    }
    
    /**
     * Sparkle effect - random sparkles on keys
     */
    private fun drawSparkle(canvas: Canvas, effect: RgbEffect, keyRects: List<RectF>) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        
        keyRects.forEachIndexed { index, rect ->
            // Use key index and time for pseudo-random sparkle
            val sparklePhase = (animationTime * speed + index * 0.1f) % 2f
            
            if (sparklePhase > 1.5f) {
                val fade = 2f - sparklePhase
                val alpha = (fade * effect.intensity * 255).toInt()
                
                paint.color = colors[index % colors.size]
                paint.alpha = alpha
                canvas.drawCircle(rect.centerX(), rect.centerY(), 6f, paint)
            }
        }
    }
    
    /**
     * Border glow - glowing border around keys
     */
    private fun drawBorderGlow(canvas: Canvas, effect: RgbEffect, keyRects: List<RectF>) {
        val colors = parseColors(effect.colors)
        val speed = effect.speed
        val phase = (animationTime * speed * 0.5f) % 1f
        val color = interpolateColors(colors, phase)
        
        glowPaint.color = color
        glowPaint.alpha = (effect.intensity * 180).toInt()
        
        keyRects.forEach { rect ->
            // Draw glow border
            canvas.drawRoundRect(rect, 8f, 8f, glowPaint)
        }
    }
    
    /**
     * Parse color strings to integers
     */
    private fun parseColors(colorStrings: List<String>): List<Int> {
        if (colorCache.size == colorStrings.size) {
            return colorCache
        }
        
        colorCache.clear()
        colorStrings.forEach { colorStr ->
            try {
                colorCache.add(Color.parseColor(colorStr))
            } catch (e: Exception) {
                colorCache.add(Color.WHITE)
            }
        }
        return colorCache
    }
    
    /**
     * Interpolate between multiple colors
     */
    private fun interpolateColors(colors: List<Int>, phase: Float): Int {
        if (colors.isEmpty()) return Color.WHITE
        if (colors.size == 1) return colors[0]
        
        val colorCount = colors.size
        val scaledPhase = phase * (colorCount - 1)
        val index1 = scaledPhase.toInt().coerceIn(0, colorCount - 2)
        val index2 = (index1 + 1).coerceIn(0, colorCount - 1)
        val blend = scaledPhase - index1
        
        return blendColors(colors[index1], colors[index2], blend)
    }
    
    /**
     * Blend two colors
     */
    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        
        val r = (r1 + (r2 - r1) * ratio).toInt()
        val g = (g1 + (g2 - g1) * ratio).toInt()
        val b = (b1 + (b2 - b1) * ratio).toInt()
        
        return Color.rgb(r, g, b)
    }
    
    /**
     * Reset animation state
     */
    fun reset() {
        animationTime = 0f
        rippleTime = 0f
    }
}
