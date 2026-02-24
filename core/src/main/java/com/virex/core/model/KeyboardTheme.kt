package com.virex.core.model

data class KeyboardTheme(
    val id: String,
    val name: String,
    val category: String,
    val background: String,
    val keyColor: String,
    val fontColor: String,
    val isPro: Boolean,
    val type: String = "static", // "static" or "animated"
    val effects: Map<String, Any> = emptyMap() // RGB effect config
)
