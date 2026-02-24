package com.virex.app.share

import android.content.Context
import android.content.Intent

object ShareHelper {
    
    fun shareTheme(context: Context, themeId: String, themeName: String) {
        val deepLink = "virexkeyboard://theme/$themeId"
        val message = "Check out this amazing keyboard theme! 🔥\n\n" +
                "$themeName\n\n" +
                "Get it here: $deepLink"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "Virex Keyboard Theme")
        }
        
        context.startActivity(Intent.createChooser(intent, "Share theme via"))
    }
    
    fun shareApp(context: Context) {
        val playStoreLink = "https://play.google.com/store/apps/details?id=com.virex.keyboard"
        val message = "Transform your keyboard with amazing RGB themes! 🎨✨\n\n" +
                "Download Virex Keyboard:\n$playStoreLink"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Virex Keyboard"))
    }
}
