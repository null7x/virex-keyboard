package com.virex.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.virex.core.emoji.EmojiData

class EmojiPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface EmojiListener {
        fun onEmojiSelected(emoji: String)
        fun onClosePanel()
    }

    private var emojiListener: EmojiListener? = null
    private var currentCategory = "smileys"
    
    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 48f
        textAlign = Paint.Align.CENTER
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1F33")
        style = Paint.Style.FILL
    }

    private val categoryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3F51FF")
        style = Paint.Style.FILL
    }

    private val emojiRects = mutableListOf<Pair<RectF, String>>()
    private val categoryRects = mutableMapOf<String, RectF>()

    fun setEmojiListener(listener: EmojiListener) {
        emojiListener = listener
    }

    fun setCategory(category: String) {
        currentCategory = category
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        
        emojiRects.clear()
        
        // Draw category tabs
        val categoryWidth = width / 4f
        val categories = listOf("smileys", "hearts", "gestures", "popular")
        categories.forEachIndexed { index, cat ->
            val rect = RectF(
                index * categoryWidth,
                0f,
                (index + 1) * categoryWidth,
                60f
            )
            categoryRects[cat] = rect
            
            if (cat == currentCategory) {
                canvas.drawRect(rect, categoryPaint)
            }
            
            val label = when (cat) {
                "smileys" -> "😀"
                "hearts" -> "❤️"
                "gestures" -> "👍"
                "popular" -> "🔥"
                else -> "😀"
            }
            
            canvas.drawText(
                label,
                rect.centerX(),
                rect.centerY() + 16f,
                emojiPaint
            )
        }

        // Draw emojis
        val emojis = when (currentCategory) {
            "smileys" -> EmojiData.smileys
            "hearts" -> EmojiData.hearts
            "gestures" -> EmojiData.gestures
            "popular" -> EmojiData.popular
            else -> EmojiData.smileys
        }

        val cols = 8
        val rowHeight = (height - 80f) / 4f
        val colWidth = width / cols.toFloat()

        emojis.take(32).forEachIndexed { index, emojiItem ->
            val row = index / cols
            val col = index % cols
            
            val rect = RectF(
                col * colWidth + 8f,
                80f + row * rowHeight + 8f,
                (col + 1) * colWidth - 8f,
                80f + (row + 1) * rowHeight - 8f
            )
            
            canvas.drawText(
                emojiItem.unicode,
                rect.centerX(),
                rect.centerY() + 16f,
                emojiPaint
            )
            
            emojiRects += rect to emojiItem.unicode
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Check category tabs
            categoryRects.forEach { (cat, rect) ->
                if (rect.contains(event.x, event.y)) {
                    setCategory(cat)
                    return true
                }
            }
            
            // Check emoji selection
            val emoji = emojiRects.firstOrNull { it.first.contains(event.x, event.y) }?.second
            if (emoji != null) {
                emojiListener?.onEmojiSelected(emoji)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
