package com.virex.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.virex.core.model.KeyboardTheme
import com.virex.core.model.RgbEffect
import com.virex.core.model.RgbEffectType
import com.virex.keyboard.theme.AnimatedThemeRenderer

class VirexKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface KeyListener {
        fun onKeyPress(value: String)
        fun onKeyLongPress(value: String) {}
        fun onKeyRelease(value: String) {}
    }

    private var keyListener: KeyListener? = null
    private var currentLanguage = "en"
    private var currentTheme: KeyboardTheme? = null
    private val animatedRenderer = AnimatedThemeRenderer()
    
    private val engKeyRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"),
        listOf("🌐", "😀", ",", "space", ".", "↵")
    )

    private val rusKeyRows = listOf(
        listOf("й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з"),
        listOf("ф", "ы", "в", "а", "п", "р", "о", "л", "д"),
        listOf("⇧", "я", "ч", "с", "м", "и", "т", "ь", "⌫"),
        listOf("🌐", "😀", ",", "space", ".", "↵")
    )

    private var keyRows = engKeyRows
    private var pressedKey: String? = null
    private val longPressDelay = 500L
    private val longPressRunnable = Runnable {
        pressedKey?.let { keyListener?.onKeyLongPress(it) }
    }

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A2F42")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 38f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private val keyRects = mutableListOf<Pair<RectF, String>>()

    fun setKeyListener(listener: KeyListener) {
        keyListener = listener
    }

    fun setLanguage(lang: String) {
        currentLanguage = lang
        keyRows = if (lang == "ru") rusKeyRows else engKeyRows
        invalidate()
    }

    fun applyTheme(theme: KeyboardTheme) {
        currentTheme = theme
        keyPaint.color = Color.parseColor(theme.keyColor)
        textPaint.color = Color.parseColor(theme.fontColor)
        
        // Setup animated theme renderer
        animatedRenderer.setTheme(theme)
        
        // Start animation loop if needed
        if (animatedRenderer.isAnimated()) {
            startRgbAnimation()
        }
        
        invalidate()
    }

    /**
     * Apply custom font typeface to keyboard
     */
    fun applyFont(typeface: Typeface) {
        textPaint.typeface = typeface
        invalidate()
    }

    /**
     * Reset to default system font
     */
    fun resetFont() {
        textPaint.typeface = Typeface.DEFAULT
        invalidate()
    }

    /**
     * Enable/disable RGB animation
     */
    fun setAnimationEnabled(enabled: Boolean) {
        animatedRenderer.setAnimationEnabled(enabled)
        if (enabled && animatedRenderer.isAnimated()) {
            startRgbAnimation()
        }
        invalidate()
    }

    private fun startRgbAnimation() {
        // Post invalidate at 60 FPS for smooth animation
        postOnAnimation {
            if (animatedRenderer.isAnimated()) {
                animatedRenderer.update()
                invalidate()
                startRgbAnimation()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        keyRects.clear()

        val rowHeight = height / keyRows.size.toFloat()
        val allKeyRects = mutableListOf<RectF>()
        
        // First pass: collect all key rectangles
        keyRows.forEachIndexed { rowIndex, row ->
            val keyWidth = width / row.size.toFloat()
            row.forEachIndexed { colIndex, key ->
                val left = colIndex * keyWidth + 8f
                val top = rowIndex * rowHeight + 8f
                val right = (colIndex + 1) * keyWidth - 8f
                val bottom = (rowIndex + 1) * rowHeight - 8f
                val rect = RectF(left, top, right, bottom)
                
                allKeyRects.add(rect)
                keyRects += rect to key
            }
        }
        
        // Draw RGB effects layer (behind keys)
        if (animatedRenderer.isAnimated()) {
            animatedRenderer.draw(canvas, width.toFloat(), height.toFloat(), allKeyRects)
        }
        
        // Second pass: draw keys
        keyRects.forEach { (rect, key) ->
            // Draw key background
            canvas.drawRoundRect(rect, 22f, 22f, keyPaint)

            // Draw key label
            val label = if (key == "space") "space" else key
            val x = rect.centerX()
            val y = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(label, x, y, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val key = keyRects.firstOrNull { it.first.contains(event.x, event.y) }?.second
                if (key != null) {
                    pressedKey = key
                    postDelayed(longPressRunnable, longPressDelay)
                    keyListener?.onKeyPress(if (key == "space") " " else key)
                    
                    // Trigger ripple effect for RGB animations
                    animatedRenderer.onKeyPress(event.x, event.y)
                    
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressRunnable)
                pressedKey?.let { keyListener?.onKeyRelease(it) }
                pressedKey = null
            }
        }
        return super.onTouchEvent(event)
    }
}
