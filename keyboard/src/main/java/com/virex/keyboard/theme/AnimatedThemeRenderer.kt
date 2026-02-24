package com.virex.keyboard.theme

import android.graphics.Canvas
import android.graphics.RectF
import com.virex.core.model.KeyboardTheme
import com.virex.core.model.RgbEffect
import com.virex.core.model.RgbEffectType
import com.virex.keyboard.effects.RgbEffectRenderer

/**
 * AnimatedThemeRenderer - Manages animated theme rendering
 * 
 * Handles:
 * - RGB effects (60 FPS)
 * - Theme transitions
 * - Effect state management
 */
class AnimatedThemeRenderer {
    
    private val rgbRenderer = RgbEffectRenderer()
    
    private var currentEffect: RgbEffect? = null
    private var isAnimating = false
    private var lastFrameTime = System.currentTimeMillis()
    
    /**
     * Set theme and extract RGB effect
     */
    fun setTheme(theme: KeyboardTheme?) {
        if (theme == null || theme.type != "animated") {
            currentEffect = null
            isAnimating = false
            return
        }
        
        // Parse RGB effect from theme effects map
        currentEffect = theme.effects["rgb"]?.let { effectData ->
            try {
                when (effectData) {
                    is Map<*, *> -> {
                        // Manual parsing of effect data (simplified without Moshi)
                        val type = (effectData["type"] as? String)?.let { 
                            try { RgbEffectType.valueOf(it) } catch (e: Exception) { RgbEffectType.NONE }
                        } ?: RgbEffectType.NONE
                        
                        RgbEffect(
                            type = type,
                            speed = (effectData["speed"] as? Number)?.toFloat() ?: 1.0f,
                            intensity = (effectData["intensity"] as? Number)?.toFloat() ?: 1.0f,
                            colors = (effectData["colors"] as? List<*>)?.mapNotNull { it as? String } ?: listOf("#FF0000", "#00FF00", "#0000FF")
                        )
                    }
                    is RgbEffect -> effectData
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
        
        isAnimating = currentEffect != null && currentEffect!!.type != RgbEffectType.NONE
        
        if (isAnimating) {
            rgbRenderer.reset()
            lastFrameTime = System.currentTimeMillis()
        }
    }
    
    /**
     * Update animation frame
     * Call this every frame for smooth 60 FPS
     */
    fun update() {
        if (!isAnimating) return
        
        val currentTime = System.currentTimeMillis()
        val deltaTime = currentTime - lastFrameTime
        lastFrameTime = currentTime
        
        rgbRenderer.update(deltaTime)
    }
    
    /**
     * Draw animated effects on canvas
     */
    fun draw(canvas: Canvas, width: Float, height: Float, keyRects: List<RectF>) {
        if (!isAnimating || currentEffect == null) return
        
        rgbRenderer.draw(canvas, currentEffect!!, width, height, keyRects)
    }
    
    /**
     * Trigger touch effect (e.g., ripple)
     */
    fun onKeyPress(x: Float, y: Float) {
        if (!isAnimating) return
        
        currentEffect?.let { effect ->
            if (effect.type == RgbEffectType.RIPPLE) {
                rgbRenderer.triggerRipple(x, y)
            }
        }
    }
    
    /**
     * Check if theme has animations
     */
    fun isAnimated(): Boolean {
        return isAnimating
    }
    
    /**
     * Get current effect for preview/info
     */
    fun getCurrentEffect(): RgbEffect? {
        return currentEffect
    }
    
    /**
     * Pause/resume animations (for battery saving)
     */
    fun setAnimationEnabled(enabled: Boolean) {
        isAnimating = enabled && currentEffect != null && currentEffect!!.type != RgbEffectType.NONE
        
        if (isAnimating) {
            lastFrameTime = System.currentTimeMillis()
        }
    }
}
