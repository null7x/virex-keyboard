package com.virex.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object ThemeBroadcaster {
    const val ACTION_THEME_CHANGED = "com.virex.ACTION_THEME_CHANGED"
    const val EXTRA_THEME_ID = "theme_id"
    const val EXTRA_KEY_COLOR = "key_color"
    const val EXTRA_FONT_COLOR = "font_color"
    const val EXTRA_BACKGROUND = "background"

    fun sendThemeChanged(context: Context, themeId: String, keyColor: String, fontColor: String, background: String) {
        val intent = Intent(ACTION_THEME_CHANGED).apply {
            putExtra(EXTRA_THEME_ID, themeId)
            putExtra(EXTRA_KEY_COLOR, keyColor)
            putExtra(EXTRA_FONT_COLOR, fontColor)
            putExtra(EXTRA_BACKGROUND, background)
        }
        context.sendBroadcast(intent)
    }

    fun createReceiver(onThemeChanged: (String, String, String, String) -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == ACTION_THEME_CHANGED) {
                    val themeId = intent.getStringExtra(EXTRA_THEME_ID) ?: return
                    val keyColor = intent.getStringExtra(EXTRA_KEY_COLOR) ?: return
                    val fontColor = intent.getStringExtra(EXTRA_FONT_COLOR) ?: return
                    val background = intent.getStringExtra(EXTRA_BACKGROUND) ?: return
                    onThemeChanged(themeId, keyColor, fontColor, background)
                }
            }
        }
    }

    fun getIntentFilter(): IntentFilter = IntentFilter(ACTION_THEME_CHANGED)
}
