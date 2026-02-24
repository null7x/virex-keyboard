package com.virex.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SuggestionBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface SuggestionListener {
        fun onSuggestionSelected(suggestion: String)
    }

    private var suggestionListener: SuggestionListener? = null
    private var suggestions = listOf<String>()
    
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0A0F1E")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private val suggestionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A2F42")
        style = Paint.Style.FILL
    }

    private val suggestionRects = mutableListOf<Pair<RectF, String>>()

    fun setSuggestionListener(listener: SuggestionListener) {
        suggestionListener = listener
    }

    fun setSuggestions(newSuggestions: List<String>) {
        suggestions = newSuggestions.take(3)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        
        suggestionRects.clear()
        
        if (suggestions.isEmpty()) return
        
        val suggestionWidth = width / suggestions.size.toFloat()
        
        suggestions.forEachIndexed { index, suggestion ->
            val left = index * suggestionWidth + 12f
            val top = 8f
            val right = (index + 1) * suggestionWidth - 12f
            val bottom = height - 8f
            
            val rect = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rect, 16f, 16f, suggestionPaint)
            
            canvas.drawText(
                suggestion,
                rect.centerX(),
                rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
            
            suggestionRects += rect to suggestion
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val suggestion = suggestionRects.firstOrNull { it.first.contains(event.x, event.y) }?.second
            if (suggestion != null) {
                suggestionListener?.onSuggestionSelected(suggestion)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
