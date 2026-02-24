package com.virex.core.model

data class AnalyticsEvent(
    val name: String,
    val timestamp: Long,
    val payload: Map<String, String> = emptyMap()
)
