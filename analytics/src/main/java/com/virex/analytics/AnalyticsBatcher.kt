package com.virex.analytics

import com.virex.core.model.AnalyticsEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticsBatcher {
    private val events = mutableListOf<AnalyticsEvent>()
    private val _bufferSize = MutableStateFlow(0)
    val bufferSize = _bufferSize.asStateFlow()

    fun track(event: AnalyticsEvent) {
        events += event
        _bufferSize.value = events.size
    }

    fun drainBatch(maxSize: Int = 20): List<AnalyticsEvent> {
        val chunk = events.take(maxSize)
        repeat(chunk.size) { events.removeAt(0) }
        _bufferSize.value = events.size
        return chunk
    }
}
