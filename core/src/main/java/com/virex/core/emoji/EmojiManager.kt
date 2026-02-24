package com.virex.core.emoji

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.emojiDataStore by preferencesDataStore(name = "emoji_prefs")

class EmojiManager(private val context: Context) {

    private val recentKey = stringPreferencesKey("recent_emojis")
    private val maxRecent = 30

    val recentEmojis: Flow<List<String>> = context.emojiDataStore.data.map { prefs ->
        prefs[recentKey]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    suspend fun addRecentEmoji(emoji: String) {
        context.emojiDataStore.edit { prefs ->
            val current = prefs[recentKey]?.split(",")?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
            current.remove(emoji)
            current.add(0, emoji)
            if (current.size > maxRecent) {
                current.removeAt(current.size - 1)
            }
            prefs[recentKey] = current.joinToString(",")
        }
    }

    fun searchEmojis(query: String): List<EmojiItem> {
        if (query.isEmpty()) return emptyList()
        val lowerQuery = query.lowercase()
        return EmojiData.getAllEmojis().filter {
            it.category.contains(lowerQuery) || it.keywords.any { kw -> kw.contains(lowerQuery) }
        }
    }

    fun getByCategory(category: String): List<EmojiItem> {
        return when (category) {
            "smileys" -> EmojiData.smileys
            "hearts" -> EmojiData.hearts
            "gestures" -> EmojiData.gestures
            "popular" -> EmojiData.popular
            else -> EmojiData.getAllEmojis()
        }
    }
}
